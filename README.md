# Learning Platform

## Требования

- Java 21+

## Документация

### Структура проекта

Модули проекта:
- core: хранит domain и data слои (entity, repository, service). **Здесь реализована обязательная часть практической работы**
- app: запускает spring приложение, тестирует использование data.sql

Архитектуру проекта можно посмотреть в файле [core_architecture.puml](./docs/core_architecture.puml):

![core_architecture](./docs/core_architecture.png)

### Тестирование

Запуск unit-тестов для core модуля:

```bash
./gradlew :core:test
```

Результаты:


Для тестирования data.sql (тестового наполнения данными):

```bash
./gradlew :app:bootRun
```

Результат:

![datasql_result](./docs/res/datasql_result.png)

## Схема базы данных

За основу была взята предложенная в задании схема, но с минорным изменениями:

![db scheme](./docs/db_scheme.png)

> Также можно посмотреть исходный файл [plantuml](./docs/db_scheme.puml)

