import subprocess

bash_command = "echo 'Hello, World!'"

result = subprocess.run(bash_command, shell=True, check=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)

if result.returncode == 0:
    print("Saída do comando:", result.stdout)
else:
    print("Erro ao executar o comando:", result.stderr)
