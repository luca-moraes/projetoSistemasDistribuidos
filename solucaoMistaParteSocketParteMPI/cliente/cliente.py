from mpi4py import MPI
import threading
import requests
import random
import sys

# urlExt = 'http://localhost:' + str(porta) + '/transferenciaExterna'
# urlInt = 'http://localhost:' + str(porta) + '/transferenciaInterna'

# url = 'http://localhost:8080/transferenciaExterna'

# paramsExt = {'clienteId': '11', 'valor': '17.5', 'chaveDestino': 'a55'}
# paramsInt = {'clienteId': '11', 'valor': '17.5', 'clienteDestino': '12'}

comm = MPI.COMM_WORLD
rank = comm.Get_rank()

# urlBase = 'http://10.210.2.206'
urlBase = 'http://localhost'

def randomChave():    
    codigoNumero = str(random.randint(1, 5000))
    codigoInstituicao = random.choice(instituicoes)
    return str(codigoInstituicao + codigoNumero)
    
def transferenciaExterna(porta):
    url = f'{urlBase}:{porta}/transferenciaExterna'
    #params = {'clienteId': str(random.randint(1, 5000)), 'valor': str(random.randint(1, 50)), 'chaveDestino': randomChave()}
    params = {'clienteId': str(random.randint(1, 5000)), 'valor': f"{random.uniform(0.0, 10.0):.2f}", 'chaveDestino': randomChave()}
    response = requests.get(url, params=params)
    if response.status_code == 200:
        data = response.json()
        print(f"Transferência Externa (Rank {rank}): {data}")
    else:
        print(f'Falha na transferência externa, código de status: {response.status_code}')

def transferenciaInterna(porta):
    url = f'{urlBase}:{porta}/transferenciaInterna'
    #params = {'clienteId': str(random.randint(1, 5000)), 'valor': str(random.randint(1, 50)), 'clienteDestino': str(random.randint(1, 5000))}
    params = {'clienteId': str(random.randint(1, 5000)), 'valor': f'{random.uniform(0.0, 10.0):.2f}', 'clienteDestino': str(random.randint(1, 5000))}
    response = requests.get(url, params=params)
    if response.status_code == 200:
        data = response.json()
        print(f"Transferência Interna (Rank {rank}): {data}")
    else:
        print(f'Falha na transferência interna, código de status: {response.status_code}')
        
def saldoTotal(porta):
    url = f'{urlBase}:{porta}/saldoTotal'
    response = requests.get(url)
    if response.status_code == 200:
        data = response.json()
        return data
    else:
        print(f'Falha no saldo, código de status: {response.status_code}')

# if len(sys.argv) != 2:
#     print("Erro: Nenhum argumento foi fornecido!")
#     sys.exit(1)

# porta = sys.argv[1]

# try:
#     porta = int(porta)
# except ValueError:
#     print("Erro: O argumento deve ser um número.")
#     sys.exit(1)
    
instiuicao = "0"

porta = rank + 1
    
if porta == 1:
    porta = 8081
    instituicao = "a"
elif porta == 2:
    porta = 8082
    instituicao = "b"
elif porta == 3:
    porta = 8083
    instituicao = "c"
else:
    print("Erro: A porta deve ser 1, 2 ou 3.")
    sys.exit(1)
    
instituicoes = ['a', 'b', 'c']    
instituicoes.remove(instituicao)

saldoInicial = saldoTotal(porta)

saldoConjunto = comm.reduce(saldoInicial, op=MPI.SUM, root=0)

print(f"Saldo total instiuição {instituicao} (Rank {rank}): {saldoInicial}")

comm.Barrier()

if(rank == 0):
    print(f"Saldo conjunto: {saldoConjunto}")

comm.Barrier()

threads = []

for i in range(0, 1000):
    threads.append(threading.Thread(target=transferenciaExterna, args=(porta,)))
    threads.append(threading.Thread(target=transferenciaInterna, args=(porta,)))
    
for thread in threads:
    thread.start()
    
for thread in threads:
    thread.join()
    
comm.Barrier()

saldoIndividual = saldoTotal(porta)

print(f"Saldo final instiuição {instituicao} (Rank {rank}): {saldoIndividual}")

comm.Barrier()

saldoGeral = comm.reduce(saldoIndividual, op=MPI.SUM, root=0)

if(rank == 0):
    print(f"Saldo geral: {saldoGeral}")

print(f"Fim do programa (Rank {rank}).")