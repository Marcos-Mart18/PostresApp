package com.marcos.postresapp.presentation.ui.fragment.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marcos.postresapp.R
import com.marcos.postresapp.presentation.ui.adapter.PedidoAdminAdapter

class PedidoAdminFragment : Fragment() {

    private lateinit var rvPedidos: RecyclerView
    private lateinit var pedidoAdapter: PedidoAdminAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_pedido_admin, container, false)

        setupRecyclerView(rootView)

        return rootView
    }

    private fun setupRecyclerView(rootView: View) {
        rvPedidos = rootView.findViewById(R.id.rvPedidos)
        rvPedidos.layoutManager = LinearLayoutManager(requireContext())
        
        pedidoAdapter = PedidoAdminAdapter(
            pedidos = emptyList(),
            onAceptar = { pedido -> 
                Toast.makeText(requireContext(), "Aceptar pedido ${pedido.idPedido}", Toast.LENGTH_SHORT).show()
            },
            onEnPreparacion = { pedido -> 
                Toast.makeText(requireContext(), "En preparación ${pedido.idPedido}", Toast.LENGTH_SHORT).show()
            },
            onListo = { pedido -> 
                Toast.makeText(requireContext(), "Listo ${pedido.idPedido}", Toast.LENGTH_SHORT).show()
            },
            onCancelar = { pedido -> 
                Toast.makeText(requireContext(), "Cancelar ${pedido.idPedido}", Toast.LENGTH_SHORT).show()
            }
        )
        
        rvPedidos.adapter = pedidoAdapter
    }
}
