package com.marcos.postresapp.presentation.ui.activity.admin

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.marcos.postresapp.R
import com.marcos.postresapp.presentation.ui.adapter.CatalogoPagerAdapter
import com.marcos.postresapp.presentation.ui.fragment.ProfileFragment
import com.marcos.postresapp.presentation.ui.fragment.admin.PedidoAdminFragment
import com.marcos.postresapp.presentation.ui.fragment.admin.CatalogoAdminFragment

class HomeAdminActivity : AppCompatActivity() {
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
            PedidoAdminFragment(),
            ProfileFragment()
        )

        val pagerAdapter = CatalogoPagerAdapter(this, fragmentList)
        viewPager.adapter = pagerAdapter

        // Vinculando el TabLayout con el ViewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Inicio"
                1 -> "Pedidos"
                2 -> "Perfil"
                else -> "Otro"
            }
        }.attach()
    }
}