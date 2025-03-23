package com.example.javaDemoPopular_bot.settings;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class BusChecker {
    public static String getInfoBus(String stopId){
        StringBuilder str = new StringBuilder();
        Document page = null;
        try {
            page = Jsoup.connect("https://yandex.kz/maps/163/astana/stops/stop__" + stopId).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Elements element = page.select("body > div.body > div.app > div > div.sidebar-container > div.sidebar-view._name_masstransit._shown._view_full > div.sidebar-view__panel > div.scroll._width_narrow > div > div.scroll__content > div > div > div.tabs-view > div.business-tab-wrapper._materialized > div > div > div > div.masstransit-stop-panel-view__brief-schedule");
        Elements vehicles = element.select(".masstransit-vehicle-snippet-view");
        for (Element vehicle : vehicles) {
            // Извлекаем номер автобуса
            String busNumber = vehicle.select(".masstransit-vehicle-snippet-view__name").text();

            // Извлекаем время прибытия
            String arrivalTime = "";
            Element timeElement = vehicle.select(".masstransit-prognoses-view__title-text").first();
            if (timeElement != null) {
                arrivalTime = timeElement.text();
            }

            // Выводим номер автобуса и время прибытия
            if(arrivalTime.equals("келеді")){
                str.append("Автобус " + busNumber + " прибывает\n");
            }else{
                arrivalTime = arrivalTime.replace("әр", "").trim();
                str.append("Автобус " + busNumber + " приедет через " + arrivalTime + "\n");
            }
        }
        return str.toString();
    }
}
