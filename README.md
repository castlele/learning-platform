# Learning Platform

## Требования

- Java 21+

## Документация

### Структура проекта

Модули проекта:
- core: хранит domain и data слои (entity, repository, service). **Здесь реализована обязательная часть практической работы**

Архитектуру проекта можно посмотреть в файле [core_architecture.puml](./docs/core_architecture.puml):

![core_architecture](./docs/core_architecture.png)

### Тестирование

Запуск unit-тестов для core модуля:

```bash
./gradlew :core:test
```

Запуск всех unit-тестов:

```bash
./gradlew test
```

## Схема базы данных

За основу была взята предложенная в задании схема, но с минорным изменениями:

![db scheme](./docs/db_scheme.png)

> Также можно посмотреть исходный файл [plantuml](./docs/db_scheme.puml)

