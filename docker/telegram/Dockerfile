# Базовый образ Python
FROM python:3.9-slim

# Установка рабочей директории
WORKDIR /app

# Копирование зависимостей
COPY /telegram/requirements.txt .

# Установка зависимостей
RUN pip install --no-cache-dir -r requirements.txt

# Копирование исходного кода
COPY /telegram .

# Команда для запуска бота
CMD ["python", "bot.py"]