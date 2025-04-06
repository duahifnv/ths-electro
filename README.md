# ТНС ЭНЕРГО - Хакатон Весна 2025
Команда **ENVELOPE TEAM**

- [Скриншоты](#Скриншоты)
- [Архитектура приложения](#Архитектура)
- [Фронтенд](#Frontend)
- [Бэкенд](#Backend)

# Скриншоты

![themechange](https://github.com/user-attachments/assets/bd91a72d-4649-46ff-8bb1-fbb00b1da942)
![image](https://github.com/user-attachments/assets/7235541c-07e3-48bb-b6f0-03a236954c5a)
![image](https://github.com/user-attachments/assets/7e4b06f9-4f71-4465-98eb-1c9cb1361dd0)
![image](https://github.com/user-attachments/assets/324ec90c-519d-4b57-b8e6-315d55879e40)
![image](https://github.com/user-attachments/assets/85eb1607-d513-499e-af51-8aec78a8727f)

# Архитектура

![arch](https://github.com/user-attachments/assets/f1d317a6-ffb5-47d3-8b17-d337470f41f6)

## Использованное программное обеспечение

![Рисунок5](https://github.com/user-attachments/assets/4a447fe4-256a-423e-b39a-be9516151af0)

# Frontend

## Страницы

### Калькулятор

Упрощенное вычисление за расчетный период
![image](https://github.com/user-attachments/assets/73a2c171-dfc9-4fe0-97f8-df440c378302)

Ручное почасовое заполнение
![image](https://github.com/user-attachments/assets/3c7ab628-a0ed-4d75-ba7a-82c8a3a54bec)

По импорту файла почасового рассчета
![image](https://github.com/user-attachments/assets/5ec9cf20-9b95-4786-8cd7-53ce41cb08c9)

### Менеджмент пользователей телеграм-бота - консультантов

![image](https://github.com/user-attachments/assets/d5442c80-2926-4627-9fd6-841670a7e661)

### Страницы ввода данных сотрудниками предприятия

![image](https://github.com/user-attachments/assets/6650d956-e770-4704-a7e8-f9e06bf6033f)
![image](https://github.com/user-attachments/assets/7c51d947-e968-4881-9316-ffc29a728a56)
![image](https://github.com/user-attachments/assets/7dbd7074-7d93-4f7a-b870-37aae77e67c8)
![image](https://github.com/user-attachments/assets/bbe96ec8-0765-4f0b-96ce-16cea16ff220)

# Backend

## Доступные API

- `http://envelope42.ru` - фронтэнд
- `http://api.envelope42.ru` - бэкэнд
- `http://grafana.envelope42.ru` - система мониторинга бэкэнда
- `http://minio.envelope42.ru` - S3 хранилище объектов

### Подключение к базе данных (`postgresql 17.2`)

Сервер: `data.envelope42.ru`<br>
Имя пользователя: `postgres`<br>
Пароль пользователя: `secret`<br>

Порты:
- `5999` - база данных `identity_service`

### Подключение к S3 хранилищу (`minio ui`)

Имя пользователя: `ROOTUSER`<br>
Пароль пользователя: `PASSWORD`<br>

> [!NOTE]
> Хранилище изображений аватарок пользователей находится в бакете `avatars-bucket`

## Первоначальная установка

1. Установите Docker Desktop на свой ПК (для daemon-а) https://docs.docker.com/desktop/setup/install/windows-install/

> [!NOTE]
> Проверить, установлен ли docker на ПК можно через команду ``docker --version``

2. Установите и запустите `git bash`

3. Клонируйте репозиторий в желаемую папку

```shell
git clone https://github.com/duahifnv/ths-electro.git
cd ths-electro
```

4. Клонируйте необходимые подмодули
```shell
git submodule update --init --recursive
```

## Скрипты для локального деплоя приложения

### Деплой среды `продакшена (prod)`
- `Доступ только к порту 80 (nginx)`

```shell
sh ./scripts/start-prod.sh
```

### Деплой среды `разработки (dev)`
- `Доступ к портам всех сервисам`
- `Доступ к pgadmin 4 для работы с базой данных`

```shell
sh ./scripts/start-dev.sh
```

### Билд фронтэнда

```shell
sh ./scripts/build-frontend.sh
```

### Запуск `pgadmin`

```shell
sh ./scripts/start-pgadmin.sh
```

### Остановка среды
```shell
sh ./scripts/stop.sh
```

## Запуск необходимых частей приложения

### Запуск браузера с фронтэндом на `React`:

```shell
sh ./scripts/browser-frontend.sh
```

### Запуск браузера с бэкэндом на `Swagger-ui`:

```shell
sh ./scripts/browser-backend.sh
```

### Запуск `pgadmin4` (`pgadmin` среда)

```shell
sh ./scripts/browser-pgadmin.sh
```

#### Авторизация:
> Логин: `admin@example.com`<br>
Пароль: `admin`

## Инструкция по подтягиванию изменений из шаблона фронтенда
Фронтенд представляет из себя [subtree](https://gist.github.com/SKempin/b7857a6ff6bddb05717cc17a44091202) на шаблонный репозиторий.

Шаблон фронтенда можно найти [здесь](https://github.com/aexra/react-envelope-base.git)

Чтобы получить последние изменения (на самом деле будет происходить мердж и с этим еще нужно будет разобраться ручками) вы пишете это:
```bash
git subtree pull --prefix frontend https://github.com/aexra/react-envelope-base.git main -m "Обновление поддерева"
```

Внутри него также располагается подмодуль так что
```bash
git submodule update --init
```

Для обновления вложенного в него подмодуля используется старый добрый `pull`.
