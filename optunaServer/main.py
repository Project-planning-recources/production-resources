import socket
import optuna
import json

HOST = "localhost"
PORT = 5000

# Optional
SAVE_HYPERPARAMS = True
SAVE_CRIT = True


def put_params(reflection, contraction, expansion):
    try:
        data_to_send = {"reflection": reflection, "contraction": contraction, "expansion": expansion}
        serialized_data = (json.dumps(data_to_send) + "\n").encode('utf-8')  # Сериализуем объект в JSON
        all_hyperparams.append(data_to_send)
        client_socket.send(serialized_data)  # Отправляем как строку
        print(f"Отправлено клиенту: {data_to_send}")
    except Exception as e:
        print(f"Ошибка при отсылке ответа: обработке клиента {e}")


def get_crit():
    try:
        # Получаем результат от клиента
        received_data = client_socket.recv(1024).decode('utf-8').strip()  # Получаем и декодируем строку
        client_json = json.loads(received_data)  # Десериализуем JSON-строку в объект
        print(f"Получено от клиента: {client_json}")
        all_criterions.append(float(client_json["crit"]))
        return all_criterions[-1]
    except Exception as e:
        print(f"Ошибка при отсылке ответа: обработке клиента {e}")


def save_hyperparams():
    with open("hyperparams.txt", "w") as resultFile:
        for i in range(0, len(all_hyperparams)):
            resultFile.write(f"({i}): {all_hyperparams[i]}\n")
        resultFile.write("------\n")
        resultFile.write(f"(BEST): {study.best_params}\n")


def save_criterions():
    with open("criterions.txt", "w") as resultFile:
        for i in range(0, len(all_criterions)):
            resultFile.write(f"({i}): {all_criterions[i]}\n")
        resultFile.write("------\n")
        resultFile.write(f"(BEST): {study.best_value}\n")


def objective(trial):
    reflection = trial.suggest_float("reflection", 0.0, 5.0)
    contraction = trial.suggest_float("contraction", -5.0, 0.0)
    expansion = trial.suggest_float("expansion", -5.0, -1.0)

    put_params(reflection, contraction, expansion)

    crit_value = get_crit()

    print(f"Критерии: {crit_value}")
    return crit_value


server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.bind((HOST, PORT))
server_socket.listen(5)
print(f"Сервер запущен на {HOST}:{PORT}, ожидаем подключения...")
client_socket, addr = server_socket.accept()
received_data = client_socket.recv(1024).decode('utf-8').strip()  # Получаем и декодируем строку
client_json = json.loads(received_data)  # Десериализуем JSON-строку в объект
n_trials = client_json["n_trials"]

all_hyperparams = []
all_criterions = []

study = optuna.create_study(sampler=optuna.samplers.TPESampler(), direction="minimize")
study.optimize(objective, n_trials=n_trials)

if SAVE_HYPERPARAMS:
    save_hyperparams()

if SAVE_CRIT:
    save_criterions()
