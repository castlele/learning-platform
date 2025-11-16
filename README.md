# Learning Platform

## Требования

- Java 21+

## Документация

### Структура проекта

Проект поделен на несколько модулей:
- core: хранит domain и data слои (entity, repository, service)

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

