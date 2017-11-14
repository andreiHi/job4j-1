package ru.job4j.orderbook;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * junior.
 *
 * @author Igor Kovalkov
 * @version 0.1
 * @since 12.11.2017
 */
public class OrderBook {

    /** main method.
     * @param args аргументы
     * @throws IOException исключения файловых операций
     */
    public static void main(String[] args) throws IOException {
        String fileName = "orders.xml";

        long start = System.currentTimeMillis();
        OrderBook orderBook = new OrderBook();
        System.out.println(orderBook.createOrderBook(orderBook.parse(fileName)));
        System.out.println(System.currentTimeMillis() - start);
    }

    /** парсер книги операций.
     * @param orderMap книга операций.
     * @return отчет
     */
    String createOrderBook(Map<String, Map<Integer, Attribute>> orderMap) {
        StringBuilder sb = new StringBuilder();
        for (String book : orderMap.keySet()) {
            sb.append("order book : \"").append(book).append("\"").append(System.lineSeparator());
            IMap bid = new IMap(), ask = new IMap(true);
            separateOrderEntries(bid, ask, new ArrayList<>(orderMap.get(book).values()));
            toOrderBook(bid, ask);
            sb.append(format(bid, ask));
        }
        return sb.toString();
    }

    /** Разделение заказов на покупку и продажу.
     * @param bid возврат заказов на продажу
     * @param ask возврат заказов на покупку
     * @param orders общий список заказов
     */
    private void separateOrderEntries(IMap bid, IMap ask, List<Attribute> orders) {

        for (Attribute order : orders) {

            int price = order.getPrice(), volume = order.getVolume();
            IMap map  = order.isBuy() ? ask : bid;

            if (!map.containsKey(price)) {
                map.put(price, volume);
            } else {
                map.addValue(price, volume);
            }
        }
    }


    /** Обрезка заказов до имеющих коммерческий смысл.
     * @param bid предложение
     * @param ask спрос
     */
    void toOrderBook(IMap bid, IMap ask) {

        IMap bidOrder = bid.newCopyFlip(), askOrder = ask.newCopyFlip();

        Iterator<Integer> itBid = bidOrder.keyIterator(), itAsk = askOrder.keyIterator();

        if (itAsk.hasNext() && itBid.hasNext()) {
            int priceBid = itBid.next(), priceAsk = itAsk.next(), rest = 0;

            while (itAsk.hasNext() && itBid.hasNext()) {

                if (Integer.compare(priceAsk, priceBid) >= 0) {
                    if (rest > 0) {
                        rest -= askOrder.get(priceAsk);
                        ask.addEntry(priceAsk, askOrder);
                        priceAsk = itAsk.next();
                    } else {
                        rest += bidOrder.get(priceBid);
                        bid.addEntry(priceBid, bidOrder);
                        priceBid = itBid.next();
                    }
                } else {
                    if (rest > 0) {
                        bid.addValue(bid.endKey(), -rest);
                    } else {
                        ask.addValue(ask.endKey(), rest);
                    }
                    break;
                }
            }
        }
    }


    /** Формирование форматированого отчета по заказам.
     * @param bid книга предложений.
     * @param ask книга спроса.
     * @return Форматированный отчет.
     */
    private String format(IMap bid, IMap ask) {
        Iterator<Integer> itBid = bid.keyIterator(), itAsk = ask.keyIterator();
        StringBuilder sb = new StringBuilder();
        sb.append("       BID       |       ASK");
        sb.append(System.lineSeparator());
        String empty = " ------------- ";
        while (itAsk.hasNext() || itBid.hasNext()) {
            sb.append(itBid.hasNext() ? formatInfo(itBid.next(), bid) : empty);
            sb.append("  |  ");
            sb.append(itAsk.hasNext() ? formatInfo(itAsk.next(), ask) : empty);
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    /** Форматирование строки заказа.
     * @param key ключ
     * @param iMap книга заказов.
     * @return форматированная строка
     */
    private String formatInfo(Integer key, IMap iMap) {
        return formatInfo(key, iMap.get(key));
    }

    /** Форматирование строки заказа.
     * @param price цена
     * @param volume количество
     * @return форматированная строка
     */
    private String formatInfo(int price, int volume) {
        return String.format("%6.2f @ %6d", 0.01 * price, volume);
    }

    /** Парсер исходного файла.
     * @param file имя файла.
     * @return Книга операций.
     * @throws IOException исключения файловых операций.
     */
    Map<String, Map<Integer, Attribute>> parse(String file) throws IOException {

        Map<String, Map<Integer, Attribute>> orders = new TreeMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while (reader.ready()) {
            String readString = reader.readLine();
            Operation operation = new Operation(readString);
            if (operation.isEntryExist()) {
                String book = operation.getEntry().getBook();
                int id = operation.getEntry().getId();
                if (operation.isAttributeExist()) {
                    if (!orders.containsKey(book)) {
                        orders.put(book, new TreeMap<>());
                    }
                    orders.get(book).put(id, operation.getAttribute());
                } else {
                    orders.get(book).remove(id);
                }
            }
        }
        return orders;
    }
}



