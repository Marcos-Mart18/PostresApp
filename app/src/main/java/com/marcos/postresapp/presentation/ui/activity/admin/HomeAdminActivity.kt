package com.marcos.postresapp.presentation.ui.activity.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.marcos.postresapp.R
import com.marcos.postresapp.domain.repository.auth.AuthRepository
import com.marcos.postresapp.presentation.ui.activity.auth.LoginActivity
import com.marcos.postresapp.presentation.ui.adapter.CatalogoPagerAdapter
import com.marcos.postresapp.presentation.ui.fragment.ProfileFragment
import com.marcos.postresapp.presentation.ui.fragment.admin.PedidoAdminFragment
import com.marcos.postresapp.presentation.ui.fragment.admin.CatalogoAdminFragment
import com.marcos.postresapp.di.ServiceLocator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeAdminActivity : AppCompatActivity() {

    private val authRepository: AuthRepository by lazy {
        ServiceLocator.getAuthRepository(this)
    }

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_admin)

        // Inicializando las vistas
        drawerLayout = findViewById(R.id.mainAdmin)
        toolbar = findViewById(R.id.toolbar)
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        setSupportActionBar(toolbar)

        // Configuración de la navegación
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Configuración del ViewPager2 y TabLayout
        val fragmentList = listOf(
            CatalogoAdminFragment(),
            com.marcos.postresapp.presentation.ui.fragment.admin.CategoriaAdminFragment(),
            PedidoAdminFragment(),
            ProfileFragment()
        )

        val pagerAdapter = CatalogoPagerAdapter(this, fragmentList)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Productos"
                1 -> "Categorías"
                2 -> "Pedidos"
                3 -> "Perfil"
                else -> "Otro"
            }
        }.attach()

        // Listener para NavigationView
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_productos -> {
                    viewPager.currentItem = 0
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_categorias -> {
                    viewPager.currentItem = 1
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_pedidos -> {
                    viewPager.currentItem = 2
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_perfil -> {
                    viewPager.currentItem = 3
                    drawerLayout.closeDrawers()
                    true
                }
                R.id.salir -> {
                    logoutUser()
                    drawerLayout.closeDrawers()
                    true
                }
                else -> false
            }
        }
    }

    private fun logoutUser() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = authRepository.logout()
                withContext(Dispatchers.Main) {
                    if (result.isSuccess) {
                        navigateToLoginScreen()
                    } else {
                        showToast("Error al cerrar sesión")
                    }
                }
            } catch (e: Exception) {
                Log.e("LogoutError", "Error en logoutUser()", e)
                withContext(Dispatchers.Main) {
                    showToast("Error de conexión")
                }
            }
        }
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
