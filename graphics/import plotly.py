import pandas as pd
import numpy as np
import re
import plotly.graph_objects as go
from plotly.subplots import make_subplots
import plotly.express as px

# Считываем данные из файлов
hyperparameters_file = "hyperparams.txt"
criteria_file = "criterions.txt"

# Функция для парсинга данных из файла гиперпараметров
def parse_hyperparameters(file_path):
    data = []
    with open(file_path, 'r') as file:
        for line in file:
            match = re.match(r"\((\d+)\): \{'reflection': ([\d\-.]+), 'contraction': ([\d\-.]+), 'expansion': ([\d\-.]+)\}", line)
            if match:
                step = int(match.group(1))
                reflection = float(match.group(2))
                contraction = float(match.group(3))
                expansion = float(match.group(4))
                data.append({'step': step, 'reflection': reflection, 'contraction': contraction, 'expansion': expansion})
    return pd.DataFrame(data)

# Функция для парсинга данных из файла критериев
def parse_criteria(file_path):
    data = []
    with open(file_path, 'r') as file:
        for line in file:
            match = re.match(r"\((\d+)\): ([\d\-.]+)", line)
            if match:
                step = int(match.group(1))
                criterion = float(match.group(2))
                data.append({'step': step, 'criterion': criterion})
    return pd.DataFrame(data)

# Чтение данных
hyperparameters = parse_hyperparameters(hyperparameters_file)
criteria = parse_criteria(criteria_file)

# Проверка на соответствие шагов
if not hyperparameters['step'].equals(criteria['step']):
    raise ValueError("Steps in the files do not match!")

# Добавляем критерий в таблицу гиперпараметров для анализа
hyperparameters['criterion'] = criteria['criterion']

# Находим минимальные значения критерия каждые 10 шагов
hyperparameters['is_min'] = False
for i in range(0, len(hyperparameters), 10):
    subset = hyperparameters.iloc[i:i+10]
    min_index = subset['criterion'].idxmin()
    hyperparameters.loc[min_index, 'is_min'] = True

# Создаем субплоты
fig = make_subplots(
    rows=1, cols=3,
    subplot_titles=("Reflection", "Contraction", "Expansion"),
    specs=[[{"type": "scatter"}, {"type": "scatter"}, {"type": "scatter"}]]
)

# График для reflection
fig.add_trace(go.Scatter(
    x=hyperparameters['step'],
    y=hyperparameters['reflection'],
    mode='lines+markers',
    name='Reflection',
    line=dict(color='blue')
), row=1, col=1)

# Выделяем минимальные значения для reflection
min_values_reflection = hyperparameters[hyperparameters['is_min']]
fig.add_trace(go.Scatter(
    x=min_values_reflection['step'],
    y=min_values_reflection['reflection'],
    mode='markers',
    name='Min Reflection',
    marker=dict(color='red', size=10)
), row=1, col=1)

# График для contraction
fig.add_trace(go.Scatter(
    x=hyperparameters['step'],
    y=hyperparameters['contraction'],
    mode='lines+markers',
    name='Contraction',
    line=dict(color='green')
), row=1, col=2)

# Выделяем минимальные значения для contraction
min_values_contraction = hyperparameters[hyperparameters['is_min']]
fig.add_trace(go.Scatter(
    x=min_values_contraction['step'],
    y=min_values_contraction['contraction'],
    mode='markers',
    name='Min Contraction',
    marker=dict(color='red', size=10)
), row=1, col=2)

# График для expansion
fig.add_trace(go.Scatter(
    x=hyperparameters['step'],
    y=hyperparameters['expansion'],
    mode='lines+markers',
    name='Expansion',
    line=dict(color='orange')
), row=1, col=3)

# Выделяем минимальные значения для expansion
min_values_expansion = hyperparameters[hyperparameters['is_min']]
fig.add_trace(go.Scatter(
    x=min_values_expansion['step'],
    y=min_values_expansion['expansion'],
    mode='markers',
    name='Min Expansion',
    marker=dict(color='red', size=10)
), row=1, col=3)

# Настройки субплотов
fig.update_layout(
    title="Hyperparameters Analysis with Criterion Minima",
    height=600, width=1200,
    showlegend=True
)

# Показываем график
#fig.show()

batch_files = [f"batchTest_{i}.csv" for i in range(10)]
# Список для хранения статистики отклонений
statistics = []

# Обрабатываем каждый файл
for file in batch_files:
    data = pd.read_csv(file, delimiter=';')  # Используем точку с запятой как разделитель
    
    # Очистка названий столбцов от лишних пробелов
    data.columns = data.columns.str.strip()
    
    # Проверяем, что столбцы есть в данных
    if 'Calculated' in data.columns and 'Benchmark' in data.columns:
        # Рассчитываем отклонение в процентах для каждой строки
        deviation = (data['Calculated'] - data['Benchmark']) / data['Benchmark'] * 100
        
        # Находим среднее отклонение
        mean_deviation = deviation.mean()
        
        # Находим 80% лучших отклонений (по значению)
        best_80_percent = deviation[deviation <= deviation.quantile(0.80)].mean()
        
        # Находим 10% худших отклонений (по значению)
        worst_20_percent = deviation[deviation >= deviation.quantile(0.80)].mean()

        # Сохраняем статистику для файла
        statistics.append({
            'file': file,
            'mean_deviation': mean_deviation,
            'best_80_percent': best_80_percent,
            'worst_20_percent': worst_20_percent
        })
    else:
        print(f"Файл {file} не содержит нужных столбцов 'Calculated' и 'Benchmark'.")

# Выводим статистику для всех файлов
print("Статистика отклонений для каждого файла:")
for stat in statistics:
    print(f"Файл {stat['file']}:")
    print(f"  Среднее отклонение = {stat['mean_deviation']:.2f}%")
    print(f"  Лучшие 80% отклонений = {stat['best_80_percent']:.2f}%")
    print(f"  Худшие 20% отклонений = {stat['worst_20_percent']:.2f}%\n")
    
# Добавляем линейные графики для каждого файла
for stat in statistics:
    fig.add_trace(go.Scatter(
        x=['Mean Deviation', 'Best 80%', 'Worst 20%'],
        y=[stat['mean_deviation'], stat['best_80_percent'], stat['worst_20_percent']],
        mode='lines+markers',
        name=stat['file'],
        line=dict(width=2),
        marker=dict(size=8)
    ))

# Настройки графика
fig.update_layout(
    title="Отклонения для каждого файла",
    xaxis_title="Тип отклонения",
    yaxis_title="Отклонение (%)",
    legend_title="Файлы",
    template="plotly_dark"
)

fig.show()