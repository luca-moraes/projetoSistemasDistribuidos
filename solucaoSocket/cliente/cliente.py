import requests

url = 'http://localhost:8080/transferenciaExterna'

params = {'clienteId': '11', 'valor': '17.5', 'chaveDestino': 'a55'}

response = requests.get(url, params=params)

if response.status_code == 200:
    data = response.json()
    print(data)
else:
    print('Falha na requisição:', response.status_code)
