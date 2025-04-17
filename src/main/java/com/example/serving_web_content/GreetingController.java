// Пакет приложения
package com.example.serving_web_content;

// Импорты необходимых классов
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

// Основной класс контроллера
@RestController // Указывает, что это REST-контроллер
@RequestMapping("/weather") // Базовый URL для всех методов контроллера
public class GreetingController {

    // Получение API ключа из конфигурационного файла
    @Value("${openweathermap.api.key}")
    private String apiKey;

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
                return new WeatherDto(
                        data.getName(), // Название города
                        data.getMain().getTemp(),
                        data.getMain().getHumidity(),
                        data.getWind().getSpeed(),
                        data.getWind().getWindDirection(data.getWind().getDeg()),
                        new WeatherDto.Coord(data.getCoord().getLat(), data.getCoord().getLon())
                );
            }
        } catch (Exception e) {
            e.printStackTrace(); // Выводим ошибку в консоль
        }
        // Возвращаем ошибку, если что-то пошло не так
        return new WeatherDto("Ошибка", 0.0);
    }

    // Метод для формирования URL запроса остается прежним
    private String buildWeatherUrl(String city) {
        return "https://api.openweathermap.org/data/2.5/weather?" +
                "q=" + city + // Параметр города
                "&appid=" + apiKey + // Ключ API
                "&units=metric&lang=ru"; // Метрическая система и русский язык
    }


    // Класс для запроса от пользователя
    public static class WeatherRequest {
        private String city; // Поле для хранения названия города

        public String getCity() {
            return city;
        }
    }

    // Класс для ответа пользователю
    public static class WeatherDto {
        private String city; // Название города
        private double temperature; // Температура
        private double humidity;
        private double windSpeed;
        private String windDirection;
        // Но это не точно
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
}

    // тут напишем ручку для получения данных КЭша
    //@GetMapping ("cache")
