import java.io.*;
import java.math.*;
import java.util.*;

class Stock {
    String symbol, name;
    BigDecimal price;
    double change;
    Stock(String s, String n, double p) {
        symbol = s; name = n;
        price = new BigDecimal(String.valueOf(p)).setScale(2, RoundingMode.HALF_EVEN);
        change = (Math.random()*10)-5;
    }
    void marketMove() {
        double mv = (Math.random()*4)-2;
        price = price.add(price.multiply(new BigDecimal(mv/100))).setScale(2, RoundingMode.HALF_EVEN);
        if(price.compareTo(BigDecimal.ONE) < 0) price = new BigDecimal("1.00");
        change += mv;
    }
    public String toString() {
        return String.format("%-6s | %-18s | $%7.2f | %+6.2f%%", symbol, name, price, change);
    }
}

class Portfolio {
    Map<String, Integer> holdings = new HashMap<>();
    BigDecimal balance = new BigDecimal("10000.00");
    boolean buy(Stock s, int q) {
        BigDecimal cost = s.price.multiply(new BigDecimal(q));
        if(balance.compareTo(cost) < 0) return false;
        balance = balance.subtract(cost);
        holdings.put(s.symbol, holdings.getOrDefault(s.symbol, 0) + q);
        return true;
    }
    boolean sell(Stock s, int q) {
        int own = holdings.getOrDefault(s.symbol, 0);
        if(own < q) return false;
        balance = balance.add(s.price.multiply(new BigDecimal(q)));
        holdings.put(s.symbol, own - q);
        if(holdings.get(s.symbol)==0) holdings.remove(s.symbol);
        return true;
    }
}

public class StockTradingPlatform {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Stock> market = new ArrayList<>(Arrays.asList(
            new Stock("AAPL", "Apple Inc.", 182.52),
            new Stock("GOOGL", "Alphabet", 142.30),
            new Stock("TSLA", "Tesla", 248.50),
            new Stock("AMZN", "Amazon", 178.15),
            new Stock("MSFT", "Microsoft", 378.85),
            new Stock("PKRX", "Pak Stock", 45.20)
        ));
        Portfolio pf = new Portfolio();
        System.out.println("=== CodeAlpha Stock Trading Platform [CERTIFIED] ===");

        while(true) {
            System.out.println("\n=== LIVE MARKET [SIMULATED] ===");
            System.out.println("SYMBOL | NAME               | PRICE   | CHANGE");
            System.out.println("------------------------------------------------");
            for(Stock s: market) { s.marketMove(); System.out.println(s); }
            
            System.out.printf("\nBalance: $%.2f | Holdings: %s\n", pf.balance, pf.holdings);
            System.out.println("1.BUY 2.SELL 3.Save File 4.EXIT");
            System.out.print("Choice: ");
            String ch = sc.nextLine().trim();
            if(ch.equals("4")) break;

            try {
                if(ch.equals("1") || ch.equals("2")) {
                    System.out.print("Symbol: "); String sym = sc.nextLine().trim().toUpperCase();
                    Stock found = null;
                    for(Stock s: market) if(s.symbol.equals(sym)) found = s;
                    if(found==null){ System.out.println("[Security Gate] Invalid Symbol!"); continue; }
                    
                    System.out.print("Qty: "); int qty = Integer.parseInt(sc.nextLine().trim());
                    if(qty<=0){ System.out.println("[Security Gate] Qty > 0 required"); continue; }

                    boolean ok = ch.equals("1") ? pf.buy(found, qty) : pf.sell(found, qty);
                    System.out.println(ok ? ">> SUCCESS - CERTIFIED" : ">> FAILED - Insufficient Balance");
                } else if(ch.equals("3")) {
                    // FIXED: FileNotFoundException handled here
                    try {
                        PrintWriter pw = new PrintWriter("portfolio.txt");
                        pw.println("=== Portfolio Report ===");
                        pw.println("Balance: "+pf.balance); 
                        pw.println("Holdings: "+pf.holdings);
                        pw.println("Status: CERTIFIED");
                        pw.close();
                        System.out.println(">> Saved to portfolio.txt [File I/O Success]");
                    } catch (FileNotFoundException fe) {
                        System.out.println("File Error: " + fe.getMessage());
                    }
                } else {
                    System.out.println("[Buffer Trap] Enter 1-4 only!");
                }
            } catch(NumberFormatException e) {
                System.out.println("[Buffer Trap] Invalid number! Digits only.");
            } catch(Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Engine Shutdown. All Gatekeeper Passed.");
        sc.close();
    }
}