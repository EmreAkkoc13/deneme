package com.example.halkaarzkarhesaplama;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button hesapla, hesapmak;
    EditText etHisseAdedi, etBirimFiyat, etGunSayisi,ertoran;
    RecyclerView rvGunler;
    GunAdapter gunAdapter;
    private InterstitialAd mInterstitialAd;
    private static final String TAG = "MainActivity";
    List<String> gunList;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // Google Mobil Reklam SDK'sını başlat
        MobileAds.initialize(this, initializationStatus -> {});
        // Reklamı yükle
        loadInterstitialAd();
        // View'leri ID'leri ile bul
        etHisseAdedi = findViewById(R.id.etHisseAdedi);
        etBirimFiyat = findViewById(R.id.etBirimFiyat);
        etGunSayisi  = findViewById(R.id.etGunSayisi);
        rvGunler     = findViewById(R.id.rvGunler);
        hesapla      = findViewById(R.id.btnHesapla);
        hesapmak     = findViewById(R.id.hesapmak);
        // Liste ve RecyclerView Adapter'ı başlat
        gunList      = new ArrayList<>();
        gunAdapter   = new GunAdapter(gunList);
        // RecyclerView'a LayoutManager ekle
        rvGunler.setLayoutManager(new LinearLayoutManager(this));
        rvGunler.setAdapter(gunAdapter);
        // Layout'un sistem çubuklarına göre hizalanmasını sağla
        ViewCompat.setOnApplyWindowInsetsListener(rvGunler, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Hesapla butonuna tıklanınca yapılacak işlemler
        hesapla.setOnClickListener(v -> showAdAndCalculate());
        // Hesapmak butonuna tıklanınca reklam göster ve ardından activity başlat
        hesapmak.setOnClickListener(v -> showAdAndOpenCalculator());
    }
    private void showAdAndCalculate() {
        if (mInterstitialAd != null) {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Reklam kapatıldığında hesaplama işlemini yap
                    calculate();
                    loadInterstitialAd(); // Reklamı tekrar yükle
                }
                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    Log.d(TAG, "Ad failed to show: " + adError.getMessage());
                    // Reklam gösterilemezse doğrudan hesaplama işlemini yap
                    calculate();
                    loadInterstitialAd(); // Reklamı tekrar yükle
                }
                @Override
                public void onAdShowedFullScreenContent() {
                    // Reklam başarıyla gösterildiğinde
                    mInterstitialAd = null; // Reklamı null yap
                }
            });
            mInterstitialAd.show(this);
        } else {
            // Reklam yüklenmediyse doğrudan hesaplama işlemini yap
            calculate();
        }
    }

    private void showAdAndOpenCalculator() {
        if (mInterstitialAd != null) {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Reklam kapatıldığında CalculatorActivity'yi başlat
                    Intent calc = new Intent(MainActivity.this, CalculatorActivity.class);
                    startActivity(calc);
                    loadInterstitialAd(); // Reklamı tekrar yükle
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    Log.d(TAG, "Ad failed to show: " + adError.getMessage());
                    // Reklam gösterilemezse doğrudan CalculatorActivity'yi başlat
                    Intent calc = new Intent(MainActivity.this, CalculatorActivity.class);
                    startActivity(calc);
                    loadInterstitialAd(); // Reklamı tekrar yükle
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Reklam başarıyla gösterildiğinde
                    mInterstitialAd = null; // Reklamı null yap
                }
            });
            mInterstitialAd.show(this);
        } else {
            // Reklam yüklenmediyse doğrudan CalculatorActivity'yi başlat
            Intent calc = new Intent(MainActivity.this, CalculatorActivity.class);
            startActivity(calc);
        }
    }

    // Hesaplama işlemini yapan fonksiyon
    private void calculate() {
        // Girdi doğrulaması
        if (!validateInput()) {
            Toast.makeText(MainActivity.this, "Lütfen zorunlu alanları doğru şekilde doldurun.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Listeyi temizle
        gunList.clear();

        // Kullanıcı girişlerini al
        int hisseAdedi = Integer.parseInt(etHisseAdedi.getText().toString());
        double baslangicFiyat = Double.parseDouble(etBirimFiyat.getText().toString());
        int gunSayisi = Integer.parseInt(etGunSayisi.getText().toString());

        double birimFiyat = baslangicFiyat;
        double toplamKar = 0;
        double gstpkar;


        // StringBuilder ile verileri daha verimli ekle
        StringBuilder gunBilgisiBuilder = new StringBuilder();
        int i = 0;

        for (i = 1; i <= gunSayisi; i++) {
            double gunlukKar = birimFiyat * 0.10; // %10 kar hesapla
            gstpkar = gunlukKar * hisseAdedi;
            toplamKar += gstpkar; // Toplam karı güncelle
            birimFiyat += gunlukKar; // Yeni fiyatı güncelle

            // Gün bilgilerini stringe ekle
            gunBilgisiBuilder.setLength(0); // StringBuilder'ı sıfırla
            gunBilgisiBuilder.append(i).append(". Gün elde edilen kar:    ")
                    .append(String.format("%.2f", gstpkar))
                    .append("     Gün Sonu Hissenin Adet Fiyatı: ")
                    .append(String.format("%.2f", birimFiyat));

            gunList.add(gunBilgisiBuilder.toString()); // Listeye ekle

        }


        // Toplam değer ve kar bilgilerini ekle
        double toplamDeger = hisseAdedi * birimFiyat;
        gunList.add("\n" + gunSayisi + " Gün Sonra Toplam Değer: " + String.format("%.2f", toplamDeger));
        gunList.add("Toplam Edilen Kar: " + String.format("%.2f", toplamKar));

        // Adapter'a veri setinin değiştiğini bildir
        gunAdapter.notifyDataSetChanged();
    }

    // Girdi doğrulama fonksiyonu
    private boolean validateInput() {
        try {
            int     hisseAdedi = Integer.parseInt(etHisseAdedi.getText().toString());
            double  birimFiyat = Double.parseDouble(etBirimFiyat.getText().toString());
            int     gunSayisi  = Integer.parseInt(etGunSayisi.getText().toString());

            return hisseAdedi > 0 && birimFiyat > 0 && gunSayisi > 0;
        } catch (NumberFormatException e) {
            return false; // Geçersiz sayı girdisi durumunda false döner
        }
    }

    // Reklam yükleme fonksiyonu
    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, "ca-app-pub-8220884523643258~7952808606", adRequest, new InterstitialAdLoadCallback() {

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.d(TAG, loadAdError.toString());
                mInterstitialAd = null;
            }

            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
                Log.i(TAG, "onAdLoaded");
            }
        });
    }
}
