# ЛР №4 — Git и Jenkins, вариант 12 «Интернет-магазин»

Основа — исходный проект ЛР №3. Сохранены существующие Jenkinsfile, pom.xml и тесты.

## Изменения в обработке товаров

В `Product.java` добавлена проверка непустого названия и неотрицательной цены. Добавлены три теста `ProductTest.java`.

## GitHub (выполнить на своём компьютере)

1. Создать **пустой** репозиторий на GitHub, без README и .gitignore.
2. Распаковать архив в отдельную папку, открыть в ней PowerShell и выполнить команды:

```powershell
git init
git branch -M main
git add .
git commit -m "Initial Maven project with Jenkins pipeline"
git remote add origin https://github.com/USERNAME/REPOSITORY.git
git push -u origin main
```

Заменить USERNAME/REPOSITORY на свои значения. Если Git попросит авторизацию, войти через браузер или использовать разрешённый способ доступа; пароль GitHub не вводить вместо токена.

Чтобы продемонстрировать **второй коммит**, после первого push можно внести небольшое изменение в `README_LAB4.md` (например, добавить строку «Проверка автосборки»), затем выполнить:

```powershell
git add README_LAB4.md
git commit -m "Update documentation to trigger Jenkins"
git push
```

## Jenkins

1. Открыть `Lab3-InternetShop` → `Configure`.
2. В разделе Pipeline сменить Definition на **Pipeline script from SCM**.
3. SCM: **Git**; Repository URL: адрес своего репозитория; Branch Specifier: `*/main`; Script Path: `Jenkinsfile`.
4. Для приватного репозитория настроить credentials Jenkins. Не вставлять токены в Jenkinsfile.
5. В разделе Build Triggers включить **Poll SCM** и указать расписание `H/2 * * * *` (проверка примерно раз в две минуты). Сохранить.
6. Нажать Build Now один раз для проверки Git checkout, затем сделать второй коммит и push. Дождаться автоматического запуска без нажатия Build Now.

**Важно:** Poll SCM не запускает сборку мгновенно после push; Jenkins периодически проверяет новые коммиты.

## Демонстрация FAILURE → SUCCESS

После успешной сборки временно изменить в `ProductTest.java` ожидаемую цену с `25.0` на `999.0`, сделать commit + push и дождаться FAILURE. Затем вернуть `25.0`, сделать ещё commit + push и дождаться SUCCESS. Не оставлять сломанный тест в итоговом репозитории.

## Скриншоты для отчёта

GitHub: файлы проекта и история минимум двух коммитов; Jenkins Configure: Pipeline from SCM и Poll SCM; Jenkins: автоматически запущенные сборки FAILURE и SUCCESS, Console Output и стадии.

**Не проверено здесь:** реальная работа GitHub/Jenkins и выполнение Maven-тестов на вашем компьютере; эти действия нужно подтвердить локально.
