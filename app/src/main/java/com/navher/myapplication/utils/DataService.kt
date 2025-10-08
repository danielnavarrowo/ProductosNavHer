package com.navher.myapplication.utils
import android.content.Context
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.navher.myapplication.BuildConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.OTP
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("products_cache")


class DataService(private val context: Context) {

    private val PRODUCTS_KEY = stringPreferencesKey("cached_products")
    private val UPDATE_KEY = stringPreferencesKey("last_update")
    var serverUpdate: LocalDate = LocalDate.parse("1969-12-12")


    // Initialize the Supabase client
    private val supabaseClient = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_KEY
    ) {
        install(Postgrest)
        install(Auth)
    }

    val auth = supabaseClient.auth

    suspend fun sendOTP(email: String): Result<Unit> {
        return try {
            auth.signInWith(OTP) {
                this.email = email
                createUser = false
            }
            Result.success(Unit)
        } catch (e: Exception) {
            if (e is io.github.jan.supabase.auth.exception.AuthRestException) {
                return if (e.statusCode == 422) Result.failure(Exception("Este correo no está registrado. Intenta con otro correo.")) else Result.failure(Exception("Error: " + e.statusCode.toString() + " " + e.errorCode))
            }
            Result.failure(e)
        }
    }

    suspend fun verifyOTP(email: String, token: String): Result<Boolean> {
        return try {
            auth.verifyEmailOtp(
                type = OtpType.Email.EMAIL,
                email = email,
                token = token
            )
            Result.success(true)
        } catch (e: Exception) {
            if (e is io.github.jan.supabase.auth.exception.AuthRestException) {
                return if (e.statusCode == 403) Result.failure(Exception("Código no válido o expirado. Inténtalo de nuevo.")) else Result.failure(Exception("Error: " + e.statusCode.toString() + " " + e.errorCode))
            }
            Result.failure(e)
        }
    }

    fun isUserLoggedIn(): Boolean {

        return auth.currentSessionOrNull() != null
    }

    // Función para cerrar sesión
    suspend fun signOut() {
        auth.signOut()
    }


    //Function to check if there is any data in the cache, and return it if it up to date.
    //If not, it will fetch the data from the API and save it to the cache.
    suspend fun getProductsList(): List<Products> {
        val cachedProducts = getCachedProductsList()
        val cachedDate = getCachedLastUpdate()
        if (!isInternetAvailable(context)) {
            Toast.makeText(context, "No hay conexión a internet. Mostrando datos almacenados.", Toast.LENGTH_LONG).show()
            serverUpdate = cachedDate
            return cachedProducts
        }
        serverUpdate = fetchLastUpdate()
        if ( cachedProducts.isNotEmpty()
            && serverUpdate <= cachedDate
            && cachedDate != LocalDate.parse("1969-12-12")
            ) return cachedProducts
        else {
                val fetchedProducts = fetchProducts()
                saveProductsList(fetchedProducts, serverUpdate)
                return fetchedProducts
            }
    }

    // Fetch products from Supabase
    private suspend fun fetchProducts(): List<Products> {
        return supabaseClient.from("productos")
            .select(columns = Columns.list("codigo, descripcion, pventa, pcosto, mayoreo, iprioridad"))
            .decodeList<Products>()
    }

    // Fetch last update date from Supabase
    private suspend fun fetchLastUpdate(): LocalDate {
        return supabaseClient.postgrest.rpc("get_date")
            .decodeAs<LocalDate>()
    }

    // Save products to cache
    private suspend fun saveProductsList(products: List<Products>, lastUpdate: LocalDate) {
        context.dataStore.edit { preferences ->
            preferences[PRODUCTS_KEY] = Json.encodeToString(products)
            preferences[UPDATE_KEY] = lastUpdate.toString()
        }
    }

    // Get products list from cache
    private suspend fun getCachedProductsList(): List<Products> {
        return context.dataStore.data.map { preferences ->
            val productsJson = preferences[PRODUCTS_KEY] ?: "[]"
            Json.decodeFromString<List<Products>>(productsJson)
        }.first()
    }

    // Get last update date from cache
    private suspend fun getCachedLastUpdate(): LocalDate {
        return context.dataStore.data.map { preferences ->
            LocalDate.parse(preferences[UPDATE_KEY] ?: "1969-12-12")
        }.first()
    }
}

@Serializable
data class Products(
    val codigo: String,
    val descripcion: String,
    val pventa: Double,
    val pcosto: Double,
    val mayoreo: Double,
    val iprioridad: Int?
)
