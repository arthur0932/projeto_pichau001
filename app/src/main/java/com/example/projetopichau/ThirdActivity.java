package com.example.projetopichau;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ThirdActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        // Configuração dos produtos para adicionar ao carrinho ao clicar na imagem
        findViewById(R.id.imageViewProduct1).setOnClickListener(v -> addToCart("Teclado Quântum", "R$ 1.500,00", R.drawable.captura_de_tela_2026_09_03_as_16_30_13));
        findViewById(R.id.imageViewProduct2).setOnClickListener(v -> addToCart("Monitor G-Force", "R$ 2.800,00", R.drawable.captura_de_tela_2026_09_03_as_16_36_09));
        findViewById(R.id.imageViewProduct3).setOnClickListener(v -> addToCart("Estação Hyper-Apex", "R$ 3.200,00", R.drawable.captura_de_tela_2026_09_03_as_16_36_34));
        findViewById(R.id.imageViewProduct4).setOnClickListener(v -> addToCart("Cloud-G Switch", "R$ 8.900,00", R.drawable.captura_de_tela_2026_09_03_as_16_36_39));

        Button btnBack = findViewById(R.id.btnBackToSecond);
        btnBack.setOnClickListener(v -> finish());

        Button btnCart = findViewById(R.id.btnViewCart);
        if (btnCart != null) {
            btnCart.setOnClickListener(v -> {
                Intent intent = new Intent(ThirdActivity.this, CartActivity.class);
                startActivity(intent);
            });
        }
    }

    private void addToCart(String name, String price, int imageResId) {
        CartManager.addProduct(new CartManager.Product(name, price, imageResId));
        Toast.makeText(this, name + " adicionado ao carrinho!", Toast.LENGTH_SHORT).show();
    }
}