# Implement Favorites Feature

Implement the favorites screen to display vehicles marked as favorites and allow viewing their details.

## User Review Required

> [!NOTE]
> The current `TABLE_FAVORITOS` schema uses names like `COL_NOMBRE_ALBUM` and `COL_ARTISTA`. I will continue using these as mapped in `DatabaseHelper` to avoid database migration issues, but I will map them to `Car` objects in the code.

## Proposed Changes

### Data Model

#### [Car.kt](file:///C:/Users/pcdis/Downloads/CardDeck._MENU_FAV/CardDeck/app/src/main/java/com/example/murycs/model/Car.kt)

- Add `category` field to `Car` data class to persist category information through the adapter.

### Database

#### [DatabaseHelper.kt](file:///C:/Users/pcdis/Downloads/CardDeck._MENU_FAV/CardDeck/app/src/main/java/com/example/murycs/DatabaseHelper.kt)

- Add `obtenerFavoritos(): List<Car>` method to query all rows from `TABLE_FAVORITOS` and return them as a list of `Car` objects.

### Adapter

#### [MakesAdapter.kt](file:///C:/Users/pcdis/Downloads/CardDeck._MENU_FAV/CardDeck/app/src/main/java/com/example/murycs/MakesAdapter.kt)

- Update the Intent in `onBindViewHolder` to use `auto.category` if it's not null, falling back to the constructor's `categoria`.

### UI and Logic

#### [HomeFragment.kt](file:///C:/Users/pcdis/Downloads/CardDeck._MENU_FAV/CardDeck/app/src/main/java/com/example/murycs/HomeFragment.kt)

- Set the `category` field on `Car` objects before passing them to the adapters.

#### [fragment_favorites.xml](file:///C:/Users/pcdis/Downloads/CardDeck._MENU_FAV/CardDeck/app/src/main/res/layout/fragment_favorites.xml)

- Replace the placeholder `TextView` with a `RecyclerView` and a "No favorites" message.
- Add a title and `ProgressBar`.

#### [FavoritesFragment.kt](file:///C:/Users/pcdis/Downloads/CardDeck._MENU_FAV/CardDeck/app/src/main/java/com/example/murycs/FavoritesFragment.kt)

- Initialize `DatabaseHelper` and `MakesAdapter`.
- Implement `onResume` to refresh the favorites list from the database.
- Show/hide the "No favorites" message based on the list content.

## Verification Plan

### Manual Verification
- **Test Favorites List**:
    1. Open the app.
    2. Go to "Home" and mark some vehicles as favorites.
    3. Navigate to the "Favorites" tab.
    4. Verify that the marked vehicles appear in the list.
- **Test Detail Navigation**:
    1. Click on a vehicle in the "Favorites" list.
    2. Verify that `DetallesActivity` opens with the correct vehicle information.
- **Test Unfavorite from Favorites**:
    1. In the "Favorites" list, click a vehicle to open details.
    2. Click "Quitar de Favoritos".
    3. Go back to the "Favorites" list and verify it's removed.
