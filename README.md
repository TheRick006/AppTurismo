# App Torurism Itanes

## Descripción General
Aplicación Android para explorar tours turísticos, consultar sus puntos de interés, guardarlos como favoritos, verlos en un calendario, agregar notas por día y gestionar preferencias. Implementa arquitectura MVVM + Clean Architecture con soporte offline-first.

### Stack técnico:

- Kotlin, Coroutines, Flow

- Room (persistencia local)

- Retrofit + Gson (consumo de API)

- Glide (carga de imágenes)

- WorkManager (sincronización en background)

- Navigation Component (navegación entre pantallas)

- Material Design (UI)

- osmdroid (mapas OpenStreetMap)

#### Backend: API PHP en Vercel + PostgreSQL en Neon.

## 1. Estructura de Paquetes
<img width="478" height="388" alt="Estructura de Carpetas Itanes" src="https://github.com/user-attachments/assets/6e08bef4-3422-4b92-9dcc-676bbe4e6138" />


### 2. Capa de Datos
#### 2.1 Entidades de Room
<table>
<th>Entidad</th> <th>Campos clave</th> <th>Propósito</th>
<tr> <td>Tour</td> <td>tourId, name, description, imageUrl, startDate, endDate, schedule</td> <td>Tour con datos temporales</td> </tr>
<tr> <td>TouristPoint</td> <td>touristPointId, tourId, name, lat, lng, imageUrls (JSON)</td> <td>Puntos turísticos del tour</td> </tr>
<tr> <td>Favorite</td> <td>targetId + type (tour / point)</td> <td>Favoritos polimórficos</td> </tr>
<tr> <td>Note</td> <td>noteId, title, content, dateKey (YYYY-MM-DD)</td> <td>Notas asociadas a un día</td> </tr>
</table>

#### 2.2 DAOs
<table>
<th>DAO</th> <th>Metodos principales</th>
<tr> <td>TourDao</td> <td>getAll(), getTourById(), getFavoriteTours(), getToursByMonth()</td></tr>
<tr> <td>TouristPointDao</td> <td>getPointsForTour(), getPointById(), getFavoritePoints()</td></tr>
<tr> <td>FavoriteDao</td> <td>insert(), delete(), isFavorite() (retorna Flow)</td></tr>
<tr> <td>NoteDao</td> <td>getNotesForDate(), getAllNotes(), getDatesWithNotes()</td></tr>
</table>

#### 2.3 API Remota
##### Endpoints:
- `GET /api/tours/getAll` → lista completa de tours con puntos e imágenes
- `GET /api/tours/getById?id=N` → tour específico
- `GET /api/tours/getByMonth?month=YYYY-MM` → tours filtrados por mes (calendario)

<strong>Mapeo JSON ↔ Kotlin:</strong> TourRemote usa `@SerializedName("start_date")` porque PostgreSQL devuelve snake_case.

#### 2.4 Tour Repository
Combina Room + API:
``` kotlin

fun getAllToursFlow(): Flow<List<Tour>> = flow {
    CoroutineScope(Dispatchers.IO).launch { syncFromApi() }
    emitAll(tourDao.getAll())  // Room emite cambios automáticamente
}
```
<strong>Patrón offline-first:</strong> emite datos locales primero, sincroniza en background, Room notifica cambios automáticamente.

### 3. Capa de presentación

#### 3.1 Fragments
<table>
<th>Fragment</th> <th>Responsabilidad</th>
<tr> <td>SplashFragment</td> <td>Logo + navegación automática a TourList</td> </tr>
<tr> <td>TourListFragment</td> <td>Lista de tours (RecyclerView + ListAdapter)</td> </tr>
<tr> <td>TourDetailFragment</td> <td>Detalle del tour, grid de puntos, botón favorito, mapa de recorrido</td> </tr>
<tr> <td>TouristPointDetailFragment</td> <td>	ViewPager de imágenes, botón favorito, abrir en Maps, compartir</td> </tr>
<tr> <td>FavoritesFragment</td> <td>TabLayout con dos tabs: Tours / Lugares favoritos</td> </tr>
<tr> <td>CalendarFragment</td> <td>Calendario mensual + tours del día + notas</td> </tr>
<tr> <td>NotesFragment</td> <td>Vista global de todas las notas</td> </tr>
<tr> <td>SettingsFragment</td> <td>Switch notas + selección de tema</td> </tr>
</table>


#### 3.2 ViewModels
<table>
<th>View Model</th> <th>Estado Expuesto</th>
<tr> <td>TourListViewModel</td> <td>LiveData&lt;Resource&lt;List&lt;Tour&gt;&gt;&gt; </td> </tr>
<tr> <td>TourDetailViewModel</td> <td>tourWithPoints + isFavorite</td> </tr>
<tr> <td>TouristPointDetailViewModel</td> <td>point + isFavorite</td> </tr>
<tr> <td>FavoritesViewModel</td> <td>favoriteTours + favoritePoints</td> </tr>
<tr> <td>CalendarViewModel</td> <td>calendarDays, selectedDayTours, notesForSelectedDay</td> </tr>
<tr> <td>SettingsViewModel</td> <td>(implícito en SettingsManager)</td> </tr>
</table>

#### 3.3 Manejo de Estados

``` kotlin
sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String) : Resource<T>()
    class Loading<T> : Resource<T>()
}
```


