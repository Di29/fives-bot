package model;

public class OrderData {

    private String category;
    private String service;
    private String option;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    @Override
    public String toString() {
        return "Категория: " + category + "\n" + "Сервис: " + service + "\n" + "Опция: " + option;
    }
}
