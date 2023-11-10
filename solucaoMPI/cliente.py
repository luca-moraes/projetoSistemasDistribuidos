import requests

url = 'https://api.exemplo.com/dados'

params = {'parametro1': 'valor1', 'parametro2': 'valor2'}

response = requests.get(url, params=params)

if response.status_code == 200:
    data = response.json()
    print(data)
else:
    print('Falha na requisição:', response.status_code)
