# Share the button

Первый локальный MVP: заблокированные Google/Apple OAuth-кнопки, гостевой вход,
список кнопок, создание с живым предпросмотром и демонстрация экрана действия.
Гостевой вход и созданные кнопки сохраняются на устройстве после перезапуска.
Отмена закрывает форму без сохранения. Черновик переживает поворот экрана.

## Структура

- `ui/welcome`, `ui/home`, `ui/create`, `ui/action` — отдельные экраны.
  Screen и Android-обёртка `*ViewModelImpl` находятся в app; ViewModel, ViewState,
  sealed Event, EventHandler и контракты — в одноимённых пакетах `shared/commonMain`.
- Головная функция Screen получает ViewModel через Hilt, подписывается на ViewState через
  collectAsStateWithLifecycle и передаёт его в перегрузку Screen(state). ViewModel публикуется
  через CompositionLocal как интерфейс EventHandler с методом onEvent.
- Screen принимает только ViewState; события отправляет через CompositionLocal.
  Общие визуальные компоненты находятся в ui/components.
- Navigation Compose управляет back stack и временем жизни ViewModel.
  Создание кнопки — dialog destination. Черновик сохраняется через SavedStateHandle;
  после закрытия диалога его ViewModel уничтожается.
- MainActivity создаёт Android-обёртку SharedViewModelImpl через by viewModels().
  ShareButtonApp публикует её как LocalNavigationEventHandler и единолично
  обрабатывает навигационные события, когда Activity находится в RESUMED.
  Экранные ViewModel отвечают только за состояние и события своего экрана;
  головные Screen отправляют переходы по результатам входа и сохранения.
- `shared/commonMain` — модели, репозитории и интерфейс LightDataStorage.
  Android-адаптер использует Preferences DataStore, зависимости собирает Hilt.
- `app/src/test` — тесты событий экранов и Android-хранилища.
  `shared/commonTest` — тесты репозиториев.
- Таргеты shared: Android, JVM, iOS ARM64 и iOS Simulator ARM64.

Android UI пока не переносится в shared. iOS-приложение и его UI ещё не созданы;
для сборки iOS-таргетов нужны macOS и Xcode.

## Проверка

```powershell
./gradlew.bat :app:assembleDebug :app:testDebugUnitTest :shared:jvmTest :app:lintDebug
```

В Android Studio запустить конфигурацию `app` (Android API 30+).
На чистой установке: «Пропустить» → «+» → цвет и три текста → «Создать».
Пример: «Скучаю» / «SOS» / «Я скучаю!». Кнопка появляется в списке;
нажатие открывает круглый элемент действия. Его нажатие показывает локальный
предпросмотр уведомления, ничего не отправляя.

OAuth, сервер, токены приглашений, участники и push-уведомления — следующие этапы.
Локальный идентификатор кнопки не является токеном приглашения.

KMP Android-модуль использует официальный плагин:
https://developer.android.com/kotlin/multiplatform/plugin

При запуске Jetpack SplashScreen остаётся видимым до завершения чтения
SessionRepository.hasEntered() в Activity-level SharedViewModel. Затем NavHost
сразу открывает home или welcome. WelcomeViewModel только обрабатывает гостевой
вход. Ошибка инициализации закрывает splash и показывает повторную попытку.
Реализации ButtonRepositoryImpl и SessionRepositoryImpl находятся в shared;
имена репозиториев не зависят от типа источника данных.

Kotlin и Compose compiler: 2.4.21; Hilt: 2.60.1. Состояния ViewModel и репозитория
объявлены как StateFlow с explicit backing field MutableStateFlow, без отдельных
private-свойств и asStateFlow(). Навигация — SharedFlow с MutableSharedFlow;
UI собирает события напрямую, включая повторные одинаковые переходы.
SharedFlow без replay не хранит события при отсутствии подписчика.

Версии Kotlin: https://kotlinlang.org/docs/releases.html


## Room + Firestore

`ButtonRepositoryImpl` и Room DAO/Entity находятся в `shared/commonMain`.
Android Hilt создаёт `ButtonDatabase` (`buttons.db`) и `FirestoreButtonDataSource`.
Room — источник данных списка; создание своей кнопки работает без сети.
При первом обращении старый JSON из DataStore переносится в Room. Исходный JSON
сохраняется, а маркер предотвращает повторный импорт; при сбое миграция повторяется.
Сессия по-прежнему хранится в DataStore.

API репозитория:

- `add(button)` — сохранить свою кнопку локально.
- `share(buttonId)` — опубликовать свою кнопку в `sharedButtons/{token}` и вернуть UUID-токен.
  Повторный вызов обновляет тот же документ. Токен возвращается только после подтверждения сервера.
- `join(token)` — скачать расшаренную кнопку и сохранить подписку в Room.
  Повторное присоединение обновляет запись, не создавая дубликат.
- `load()` — обновить сохранённые подписки с сервера. При ошибке кэш остаётся доступен;
  ошибка передаётся вызывающему коду. Главный экран вызывает обновление при открытии и по Retry.
- `buttons` — Flow актуального содержимого Room, включая свои и полученные кнопки.

Токен даёт право чтения всем, кому его передали. Запись разрешена только владельцу.
Перечисление коллекции запрещено. Это MVP с доступом по токену, без реестра участников,
push-уведомлений, автоматической фоновой синхронизации и UI для отправки/ввода токена.
Свои кнопки публикуются при `share`, а не автоматически при локальном сохранении.

Настройка Firebase для Android:

1. Используется существующий `app/google-services.json`.
2. В Firebase Console создать Cloud Firestore и включить Authentication → Anonymous.
3. Опубликовать `firestore.rules` в том же проекте (например, `firebase deploy --only firestore:rules --project <project-id>`).
4. Проверить на двух установках: `add` → `share` на первой, `join(token)` на второй;
   перезапустить вторую без сети и проверить локальный кэш.

Конфигурация и правила добавлены в репозиторий, но не развёрнуты в облаке автоматически.
Anonymous Auth создаётся лениво при первом облачном запросе. После удаления данных приложения
анонимная учётная запись владельца теряется; восстановление через OAuth пока не реализовано.
Firebase Android SDK остаётся в Android-адаптере. Для iOS потребуется реализация
`ButtonRemoteDataSource` и создание Room builder на стороне iOS.

Документация: [Room KMP](https://developer.android.com/kotlin/multiplatform/room),
[Firebase Android](https://firebase.google.com/docs/android/setup).


## Общие ViewModel

`shared/commonMain/ui/{welcome,home,create,action}` содержит события, состояния,
обработчики событий и всю экранную логику. `SharedViewModel` и события навигации
также находятся в commonMain. Здесь нет Compose, Hilt, Android resources,
Jetpack ViewModel или SavedStateHandle.

`*ViewModelImpl` в app наследуются от Jetpack ViewModel и делегируют
`*ViewModelContract` общей реализации. Контракт наследует `CommonViewModel`
и экранный EventHandler. Android-обёртка переопределяет `coroutineScope` через
`viewModelScope`, затем в init вызывает `delegate.attach(coroutineScope)`.
Это явное подключение необходимо: Kotlin delegation не перенаправляет
внутренние обращения делегата к переопределённым свойствам обёртки.
Подписки запускаются после attach; при очистке Jetpack ViewModel все общие
корутины отменяются вместе с её scope. Общая реализация не создаёт свой scope.
На другой платформе нужно передать scope, принадлежащий её lifecycle.

`ViewModelSavedState` — общий интерфейс для состояния черновика и выбора действия.
Android-адаптер работает с SavedStateHandle. `ViewError` хранит тип ошибки;
Android сопоставляет его с локализованным ресурсом через `messageRes`.
CompositionLocal остаются в app и принимают общие интерфейсы обработчиков.

Проверка общей логики и Android-делегирования:

```powershell
./gradlew.bat :shared:testAndroidHostTest :app:testDebugUnitTest --tests '*ViewModel*Test'
```

`ScreenViewModelsTest` перенесён в commonTest. Android `ViewModelDelegationTest`
проверяет отмену общего коллектора при ViewModelStore.clear() и восстановление
черновика через SavedStateHandle.
