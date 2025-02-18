package com.example.prototype;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresPermission;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.prototype.Api.Dto.UserAccountDto;
import com.example.prototype.Api.Dto.points.PointOfInterestDto;
import com.example.prototype.Api.Dto.points.PointOfInterestListDto;
import com.example.prototype.Api.Dto.points.PointsSurveyDto;
import com.example.prototype.Api.VkrService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import static android.content.Context.LOCATION_SERVICE;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;


public class ScrollingFragmentSelections extends Fragment implements MapObjectTapListener {
    private MapView mapView;
    private ProgressBar progressBar;
    private TextView textViewInfo;
    private CardView cardView;
    private ImageButton imageButtonProfile;
    private LinearLayout layoutProfilePanel;
    private EditText editTextRadius;
    private CheckBox checkboxFitnessCentre;
    private CheckBox checkboxSportsCentre;
    private CheckBox checkboxFitnessStation;
    private CheckBox checkboxTrack;
    private CheckBox checkboxSwimming;
    private Button buttonSave;
    private Long userId;
    private PointsSurveyDto pointsSurveyDto;
    private Point pointOfUser;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int REQUEST_ENABLE_LOCATION = 123;
    CompositeDisposable disposable = new CompositeDisposable();
    public final VkrService vkrService = new VkrService();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_scrolling_selections, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getLong("user_id", 0);

        mapView = view.findViewById(R.id.mapview);
        progressBar = view.findViewById(R.id.progres);
        textViewInfo = view.findViewById(R.id.text_view_info);
        imageButtonProfile = view.findViewById(R.id.image_button_profile);
        layoutProfilePanel = view.findViewById(R.id.layout_profile_panel);
        cardView = view.findViewById(R.id.card_view_users_data);

        editTextRadius = view.findViewById(R.id.edit_text_radius);
        checkboxFitnessCentre = view.findViewById(R.id.checkbox_fitness_centre);
        checkboxSportsCentre = view.findViewById(R.id.checkbox_sports_centre);
        checkboxFitnessStation = view.findViewById(R.id.checkbox_fitness_station);
        checkboxTrack = view.findViewById(R.id.checkbox_track);
        checkboxSwimming = view.findViewById(R.id.checkbox_swimming);
        buttonSave = view.findViewById(R.id.button_save);

        checkPermissionAndGps();

        // Обработчики нажатий
        imageButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProfilePanel();
            }
        });
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Сохранить изменения профиля
                PointsSurveyDto pointsDto = new PointsSurveyDto();
                if (pointsSurveyDto != null) pointsDto.setId(pointsSurveyDto.getId());
                pointsDto.setUserId(userId);
                if (!TextUtils.isEmpty(editTextRadius.getText())) {
                    int value = 1000;
                    try {
                        value = Integer.parseInt(String.valueOf(editTextRadius.getText()));
                    }
                    catch (Exception e) {
                        Toast.makeText(getActivity(), "Радиус указан неверно", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (value < 1000) pointsDto.setRadius(1000);
                    else pointsDto.setRadius(value);
                }
                else {
                    pointsDto.setRadius(1000);
                }

                pointsDto.setFitnessCentres(checkboxFitnessCentre.isChecked());
                pointsDto.setSportsCentres(checkboxSportsCentre.isChecked());
                pointsDto.setFitnessStations(checkboxFitnessStation.isChecked());
                pointsDto.setTracks(checkboxTrack.isChecked());
                pointsDto.setSwimmings(checkboxSwimming.isChecked());

                updateOrCreatePointsSurvey(pointsDto);
            }
        });

        return view;
    }

    public void updateOrCreatePointsSurvey(PointsSurveyDto pointsDto) {
        disposable.add(vkrService.getApi().CreateOrUpdatePointsSurvey(pointsDto)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<PointsSurveyDto, Throwable>() {
                    @Override
                    public void accept(PointsSurveyDto pointsSurveyDto, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            Toast.makeText(getActivity(), "Data loading error updateOrCreatePointsSurvey", Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            Toast.makeText(getActivity(), "Успешно", Toast.LENGTH_SHORT).show();
                            getPointsSurvey();
                        }
                    }
                }));
    }

    public void getPointsSurvey(){
        disposable.add(vkrService.getApi().GetPointsSurveyByUserAccount(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<PointsSurveyDto, Throwable>() {
                    @Override
                    public void accept(PointsSurveyDto pointsSurvey, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            //Toast.makeText(getActivity(), "Data loading error getPointsSurvey", Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else if (pointsSurvey != null) {
                            pointsSurveyDto = pointsSurvey;
                            Log.e("ff0", pointsSurveyDto.toString());
                            editTextRadius.setText(String.valueOf(pointsSurveyDto.getRadius()));
                            checkboxFitnessCentre.setChecked(pointsSurveyDto.getFitnessCentres());
                            checkboxSportsCentre.setChecked(pointsSurveyDto.getSportsCentres());
                            checkboxFitnessStation.setChecked(pointsSurveyDto.getFitnessStations());
                            checkboxTrack.setChecked(pointsSurveyDto.getTracks());
                            checkboxSwimming.setChecked(pointsSurveyDto.getSwimmings());
                            getPoints(pointsSurveyDto);
                        }
                    }
                }));
    }

    public void getPoints(PointsSurveyDto pointsSurvey){
        pointsSurvey.setLatitude(pointOfUser.getLatitude());
        pointsSurvey.setLongtitude(pointOfUser.getLongitude());
        disposable.add(vkrService.getApi().SearchPointsNearby(pointsSurvey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<PointOfInterestListDto, Throwable>() {
                    @Override
                    public void accept(PointOfInterestListDto pointOfInterestListDto, Throwable throwable) throws Exception {
                        if (throwable != null) {
                            Toast.makeText(getActivity(), "Data loading error", Toast.LENGTH_SHORT).show();
                            System.out.println(throwable.getMessage());
                        } else {
                            showMarkers(pointOfInterestListDto);
                        }
                    }
                }));
    }

    private void showProfilePanel() {
        if (layoutProfilePanel.getVisibility() == View.VISIBLE) {
            layoutProfilePanel.setVisibility(View.GONE);
        } else {
            layoutProfilePanel.setVisibility(View.VISIBLE);
        }
    }

    private void showMarkers(PointOfInterestListDto pointOfInterestListDto) {
        MapObjectCollection mapObjects = mapView.getMap().getMapObjects();
        mapObjects.clear();
        if (pointOfInterestListDto.getFitnessCentres() != null) {
            for (PointOfInterestDto poi : pointOfInterestListDto.getFitnessCentres()) {

                Drawable iconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_location_on_30);
                iconDrawable.setTint(Color.GREEN);
                Bitmap iconBitmap = Bitmap.createBitmap(iconDrawable.getIntrinsicWidth(), iconDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(iconBitmap);
                iconDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                iconDrawable.draw(canvas);

                ImageProvider iconProvider = ImageProvider.fromBitmap(iconBitmap);
                PlacemarkMapObject marker = mapObjects.addPlacemark(new Point(poi.getLatitude(), poi.getLongtitude()), iconProvider);
                marker.setUserData(poi);
                marker.addTapListener(this);

            }
        }
        if (pointOfInterestListDto.getSportsCentres() != null) {
            for (PointOfInterestDto poi : pointOfInterestListDto.getSportsCentres()) {

                Drawable iconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_location_on_30);
                iconDrawable.setTint(Color.BLUE);
                Bitmap iconBitmap = Bitmap.createBitmap(iconDrawable.getIntrinsicWidth(), iconDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(iconBitmap);
                iconDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                iconDrawable.draw(canvas);

                ImageProvider iconProvider = ImageProvider.fromBitmap(iconBitmap);
                PlacemarkMapObject marker = mapObjects.addPlacemark(new Point(poi.getLatitude(), poi.getLongtitude()), iconProvider);
                marker.setUserData(poi);
                marker.addTapListener(this);

            }
        }
        if (pointOfInterestListDto.getFitnessStations() != null) {
            for (PointOfInterestDto poi : pointOfInterestListDto.getFitnessStations()) {

                Drawable iconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_location_on_30);
                iconDrawable.setTint(Color.RED);
                Bitmap iconBitmap = Bitmap.createBitmap(iconDrawable.getIntrinsicWidth(), iconDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(iconBitmap);
                iconDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                iconDrawable.draw(canvas);

                ImageProvider iconProvider = ImageProvider.fromBitmap(iconBitmap);
                PlacemarkMapObject marker = mapObjects.addPlacemark(new Point(poi.getLatitude(), poi.getLongtitude()), iconProvider);
                marker.setUserData(poi);
                marker.addTapListener(this);

            }
        }
        if (pointOfInterestListDto.getTracks() != null) {
            for (PointOfInterestDto poi : pointOfInterestListDto.getTracks()) {

                Drawable iconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_location_on_30);
                iconDrawable.setTint(Color.BLACK);
                Bitmap iconBitmap = Bitmap.createBitmap(iconDrawable.getIntrinsicWidth(), iconDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(iconBitmap);
                iconDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                iconDrawable.draw(canvas);

                ImageProvider iconProvider = ImageProvider.fromBitmap(iconBitmap);
                PlacemarkMapObject marker = mapObjects.addPlacemark(new Point(poi.getLatitude(), poi.getLongtitude()), iconProvider);
                marker.setUserData(poi);
                marker.addTapListener(this);

            }
        }
        if (pointOfInterestListDto.getSwimmings() != null) {
            for (PointOfInterestDto poi : pointOfInterestListDto.getSwimmings()) {

                Drawable iconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_location_on_30);
                iconDrawable.setTint(Color.CYAN);
                Bitmap iconBitmap = Bitmap.createBitmap(iconDrawable.getIntrinsicWidth(), iconDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(iconBitmap);
                iconDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                iconDrawable.draw(canvas);

                ImageProvider iconProvider = ImageProvider.fromBitmap(iconBitmap);
                PlacemarkMapObject marker = mapObjects.addPlacemark(new Point(poi.getLatitude(), poi.getLongtitude()), iconProvider);
                marker.setUserData(poi);
                marker.addTapListener(this);

            }
        }
    }

    @Override
    public boolean onMapObjectTap(MapObject mapObject, Point point) {
        Object userData = mapObject.getUserData();
        PointOfInterestDto poi = (PointOfInterestDto) userData;
        String info = poi.getDescription();
        if (poi.getName() != null) info += " - " + poi.getName();
        textViewInfo.setText(info);
        return true;
    }

    //Проверка разрешений и gps
    private void checkPermissionAndGps() {
        // Проверка разрешения и включенности GPS
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // GPS включен, получаем местоположение пользователя
                requestSingleLocationUpdate();
            } else {
                // GPS выключен, предложим пользователю включить его
                showGPSDisabledAlert();
            }
        } else {
            // Запрос разрешения на доступ к местоположению
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    //После получения разрешения
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение получено, проверяем включен ли GPS
                checkGPSStatus();
            } else {
                Toast.makeText(requireContext(), "Для отображения вашего местоположения необходимо разрешение", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Проверяет включенность GPS
    @SuppressLint("MissingPermission")
    private void checkGPSStatus() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // GPS включен, получаем местоположение пользователя
            requestSingleLocationUpdate();
        } else {
            // GPS выключен, предложим пользователю включить его
            showGPSDisabledAlert();
        }
    }

    // Метод для запроса местоположения пользователя
    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    private void requestSingleLocationUpdate() {
        progressBar.setVisibility(View.VISIBLE);
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    // Местоположение пользователя успешно получено, отображаем его на карте
                    showLocationOnMap(location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(String provider) {}

                @Override
                public void onProviderDisabled(String provider) {}
            }, null);
        }
    }

    // Метод для отображения местоположения пользователя на карте
    private void showLocationOnMap(Location location) {
        pointOfUser = new Point(location.getLatitude(), location.getLongitude());
        mapView.getMap().move(
                new CameraPosition(
                        new Point(location.getLatitude(), location.getLongitude()),
                        13.0f, // Зум
                        0.0f, // Азимут
                        0.0f // Тангаж
                ),
                new Animation(Animation.Type.SMOOTH, 0), // Анимация
                null // Обратный вызов
        );
        progressBar.setVisibility(View.GONE);
        getPointsSurvey();
    }

    // Показывает диалоговое окно, предлагающее пользователю включить GPS
    private void showGPSDisabledAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
        alertDialogBuilder.setMessage("GPS выключен. Хотите включить его?")
                .setCancelable(false)
                .setPositiveButton("Да", (dialog, id) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, REQUEST_ENABLE_LOCATION);
                })
                .setNegativeButton("Нет", (dialog, id) -> {dialog.cancel(); Toast.makeText(requireContext(), "Для отображения вашего местоположения необходимо включить Gps", Toast.LENGTH_SHORT).show();});
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    //Закрытие Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    @SuppressLint("MissingPermission")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_LOCATION) {
            LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
            if (!(locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))) {
                Toast.makeText(requireContext(), "Для отображения вашего местоположения необходимо включить Gps", Toast.LENGTH_SHORT).show();
            } else {
                requestSingleLocationUpdate();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
    }
}