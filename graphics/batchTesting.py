import pandas as pd
import numpy as np
import re
import plotly.graph_objects as go
from plotly.subplots import make_subplots
import plotly.express as px

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
    
# Добавляем графики для каждого типа отклонения
    file_numbers = [stat['file'] for stat in statistics]
    mean_deviations = [stat['mean_deviation'] for stat in statistics]
    best_80_percent_deviations = [stat['best_80_percent'] for stat in statistics]
    worst_20_percent_deviations = [stat['worst_20_percent'] for stat in statistics]    
fig = go.Figure()
# Добавляем линейные графики для каждого файла
fig.add_trace(go.Scatter(
        x=file_numbers,
        y=mean_deviations,
        mode='lines+markers',
        name='Среднее отклонение',
        line=dict(width=2),
        marker=dict(size=8)
    ))

    # Лучшие 80%
fig.add_trace(go.Scatter(
        x=file_numbers,
        y=best_80_percent_deviations,
        mode='lines+markers',
        name='Лучшие 80% отклонений',
        line=dict(width=2),
        marker=dict(size=8)
    ))

 # Худшие 10%
fig.add_trace(go.Scatter(
        x=file_numbers,
        y=worst_20_percent_deviations,
        mode='lines+markers',
        name='Худшие 20% отклонений',
        line=dict(width=2),
        marker=dict(size=8)
    ))

    # Настройки графика
fig.update_layout(
        title="Статистика отклонений по файлам",
        xaxis_title="Номер файла",
        yaxis_title="Отклонение (%)",
        legend_title="Тип отклонения",
        template="plotly_dark"
    )

fig.show()