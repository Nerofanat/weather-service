// Пакет приложения
package com.example.serving_web_content;

// Импорты необходимых классов
import com.example.serving_web_content.Domain.Message;
import com.example.serving_web_content.repose.MessageRepo;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import org.springframework.http.MediaType;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

// Основной класс контроллера
@RestController // Указывает, что это REST-контроллер
@RequestMapping("/weather") // Базовый URL для всех методов контроллера
public class GreetingController {


    @Autowired
    private MessageRepo messageRepo;

    // Получение API ключа из конфигурационного файла
    @Value("${openweathermap.api.key}")
    private String apiKey;

    //Список городов которые нужны нам для сохранения данным по ним в кэш
    private final List<String> validCities = Arrays.asList("Москва", "Лондон", "Париж", "Берлин", "Рим", "Мадрид", "Токио", "Нью-Йорк", "Пекин", "Канберра");

    // Создаем простой кэш без механизма загрузки
    private final Cache<String, WeatherDto> weatherCache =
            CacheBuilder.newBuilder()
                    .maximumSize(10)           // Максимальный размер кэша
                    .expireAfterWrite(30, TimeUnit.MINUTES) // Срок жизни записи (30 минут)
                    .build();

    // Метод для получения погоды
    @GetMapping // GET-метод
    public WeatherDto getWeather(@RequestParam("city") String city) {
        try {
            // Получаем город прямо из параметра запроса
            String cityName = city;

            // Формируем URL для запроса к OpenWeatherMap API
            String url = buildWeatherUrl(cityName);

            // Создаем объект для работы с REST API
            RestTemplate restTemplate = new RestTemplate();

            // Отправляем GET-запрос к API и получаем ответ
            ResponseEntity<WeatherData> response = restTemplate.getForEntity(url, WeatherData.class);

            // Проверяем статус ответа
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                // Получаем тело ответа
                WeatherData data = response.getBody();
                // Сохраняем в базу данных
                saveToDatabase(data);

                // Формируем объект для отправки клиенту
                WeatherDto weather = new WeatherDto(
                        data.getName(), // Название города
                        data.getMain().getTemp(),
                        data.getMain().getHumidity(),
                        data.getWind().getSpeed(),
                        data.getWind().getWindDirection(data.getWind().getDeg()),
                        new WeatherDto.Coord(data.getCoord().getLat(), data.getCoord().getLon())
                );

                // Кэшируем данные, если город разрешён
                if (validCities.contains(city)) {
                    weatherCache.put(city, weather);
                }

                return weather; // Возврат сформированного объекта
            }
        } catch (Exception e) {
            e.printStackTrace(); // Выводим ошибку в консоль
        }
        // Возвращаем ошибку, если что-то пошло не так
        return new WeatherDto("Ошибка", 0.0);
    }

    //Метод сохранения в БД
    private void saveToDatabase(WeatherData data) {
        Message record = new Message();
        record.setCity(data.getName());
        record.setTemperature(data.getMain().getTemp());
        record.setHumidity(data.getMain().getHumidity());
        record.setWindSpeed(data.getWind().getSpeed());
        record.setWindDirection(data.getWind().getWindDirection(data.getWind().getDeg()));
        record.setLatitude(data.getCoord().getLat());
        record.setLongitude(data.getCoord().getLon());

        messageRepo.save(record);
    }

    // Метод для формирования URL запроса
    private String buildWeatherUrl(String city) {
        return "https://api.openweathermap.org/data/2.5/weather?" +
                "q=" + city + // Параметр города
                "&appid=" + apiKey + // Ключ API
                "&units=metric&lang=ru"; // Метрическая система и русский язык
    }


    // Класс для ответа пользователю
    public static class WeatherDto {
        private String city; // Название города
        private double temperature; // Температура
        private double humidity;
        private double windSpeed;
        private String windDirection;
        private Coord coordinates;

        //Конструктор под ошибку
        public WeatherDto(String city, double temperature) {
            this.city = city;
            this.temperature = temperature;
        }



        //тут создать полноценный конструктор на все поля
        public WeatherDto(String city, double temperature, double humidity, double windSpeed, String windDirection,  Coord coordinates) {
            this.city = city;
            this.temperature = temperature;
            this.humidity = humidity;
            this.windSpeed = windSpeed;
            this.windDirection = windDirection;
            this.coordinates = coordinates;
        }

        public double getHumidity() {
            return humidity;
        }

        public String getWindDirection() {
            return windDirection;
        }

        public double getWindSpeed() {
            return windSpeed;
        }

        public Coord getCoordinates() {
            return coordinates;
        }

        public String getCity() {
            return city;
        }

        public double getTemperature() {
            return temperature;
        }

        // Вложенный класс для хранения температуры
        public static class Coord {
            private double latitude;
            private double longitude;

            public Coord(double latitude, double longitude) {
                this.latitude = latitude;
                this.longitude = longitude;
            }

            public double getLatitude() {
                return latitude;
            }

            public double getLongitude() {
                return longitude;
            }
        }
    }

    // Класс для хранения данных, полученных от OpenWeatherMap
    public static class WeatherData {
        private String name; // Название города
        private Main main; // Основные данные о погоде
        //Добавляем все нужные поля
        private  Coordinates coord;
        private Wind wind;

        public  Coordinates getCoord() {
            return coord;
        }

        public Wind getWind() {
            return wind;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Main getMain() {
            return main;
        }

        //public void setMain(Main main) {
        //    this.main = main;
        //}

        // Вложенный класс для хранения температуры
        public static class Main {
            private double temp;// Температура
            private double humidity;

            public double getTemp() {
                return temp;
            }

            public double getHumidity() {
                return humidity;
            }

            //public void setTemp(double temp) {
            //    this.temp = temp;
        }

        // Вложенный класс кооррдинат
        public static class Coordinates {
            private double lat;
            private double lon;


            public double getLon() {
                return lon;
            }

            public double getLat() {
                return lat;
            }

        }

        // вложенный класс Ветер
        public static class Wind {
            private double speed;
            private double deg;

            public double getSpeed() {
                return speed;
            }

            public double getDeg() {
                return deg;
            }

            public String getWindDirection(double deg) {

                if (deg >= 337.5 || deg < 22.5) {
                    return "С (Северный)";
                } else if (deg >= 22.5 && deg < 67.5) {
                    return "СВ (Северо-Восточный)";
                } else if (deg >= 67.5 && deg < 112.5) {
                    return "В (Восточный)";
                } else if (deg >= 112.5 && deg < 157.5) {
                    return "ЮВ (Юго-Восточный)";
                } else if (deg >= 157.5 && deg < 202.5) {
                    return "Ю (Южный)";
                } else if (deg >= 202.5 && deg < 247.5) {
                    return "ЮЗ (Юго-Западный)";
                } else if (deg >= 247.5 && deg < 292.5) {
                    return "З (Западный)";
                } else if (deg >= 292.5 && deg < 337.5) {
                    return "СЗ (Северо-Западный)";
                }
                return "Неизвестно";
            }

        }
    }
    // тут напишем ручку для получения данных КЭша
    //@GetMapping ("cache")
    @GetMapping(value="/cache", produces=MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> getAllCachedWeather() {
        Collection<WeatherDto> values = weatherCache.asMap().values(); // Читаем все доступные записи из кэша
        if (values.isEmpty()) {
            return Collections.singletonList(Map.of("message", "No data in cache"));
        }
        return values.stream() //Преобразует исходную коллекцию (Collection<WeatherDto>) в поток данных, позволяя применять последующие операции (такие как map и collect).
                .map(this::convertToOutputFormat) //Применяет к каждому элементу потока метод convertToOutputFormat, который преобразует объект WeatherDto в карту (с нужными полями для удобства клиента).
                .collect(Collectors.toList()); //Собирает преобразованные элементы (карты) в обычный список (List<Map<String,Object>>), который можно передать клиенту или сохранить для дальнейшего использования.
    }

    // Метод конвертации для нужного формата
    private Map<String, Object> convertToOutputFormat(WeatherDto dto) {
        Map<String, Object> result = new HashMap<>();
        result.put("city", dto.getCity());
        result.put("temperature", dto.getTemperature());
        result.put("humidity", dto.getHumidity());
        result.put("windDirection", dto.getWindDirection());
        result.put("windSpeed", dto.getWindSpeed());
        result.put("coordinates", Map.of(
                "latitude", dto.getCoordinates().getLatitude(),
                "longitude", dto.getCoordinates().getLongitude()
        ));
        return result;
    }
}

