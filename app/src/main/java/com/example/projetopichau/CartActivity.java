package com.example.projetopichau;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        LinearLayout container = findViewById(R.id.cartItemsContainer);
        TextView textTotal = findViewById(R.id.textTotal);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnFinalize = findViewById(R.id.btnFinalize);

        List<CartManager.Product> items = CartManager.getCartItems();
        double total = 0;

        for (CartManager.Product product : items) {
            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.HORIZONTAL);
            itemLayout.setPadding(0, 16, 0, 16);
            itemLayout.setGravity(Gravity.CENTER_VERTICAL);

            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(150, 150);
            imgParams.setMargins(0, 0, 24, 0);
            imageView.setLayoutParams(imgParams);
            imageView.setImageResource(product.imageResId);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            itemLayout.addView(imageView);

            TextView textView = new TextView(this);
            textView.setText(String.format("%s\n%s", product.name, product.price));
            textView.setTextColor(0xFFFFFFFF);
            textView.setTextSize(16);
            itemLayout.addView(textView);

            container.addView(itemLayout);

            try {
                String priceStr = product.price.replace("R$", "").replace(".", "").replace(",", ".").trim();
                total += Double.parseDouble(priceStr);
            } catch (Exception e) {
            }
        }

        textTotal.setText(String.format(Locale.getDefault(), "Total: R$ %.2f", total));

        btnBack.setOnClickListener(v -> finish());
        btnFinalize.setOnClickListener(v -> {
            Toast.makeText(this, "Redirecionando para localização...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CartActivity.this, GpsActivity.class);
            startActivity(intent);
            CartManager.clearCart();
            finish();
        });
    }
}