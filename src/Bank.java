/**
 * Created by martin on 3/4/17.
 */
public class Bank {
    public static void main(String[] args) {
        Account a = new Account();
        new Thread(new Branch(a)).start();
        new Thread(new Branch(a)).start();
    }
}

class Account {
    private float balance;

    public  synchronized void deposit(float amount) {
        // deposit money
        balance += amount;
        System.out.println("[Deposit] Current balance: " + balance);
    }

    public synchronized void withdraw(float amount) {
        // take out money
        balance -= amount;
        System.out.println("[Withdraw] Current balance: " + balance);
    }

    public float getBalance() { return this.balance; }
}

class Branch implements Runnable {
    private Account a;

    public Branch(Account a) {
        this.a = a;
    }

    public void run() {
        a.deposit(100);
        a.withdraw(90);
    }
}
