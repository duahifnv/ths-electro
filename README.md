# Envelope Web

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
git clone https://github.com/duahifnv/envelope-web.git
cd envelope-web
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
