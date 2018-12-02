package bgu.spl.mics.application.passiveObjects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InventoryTest {
    Inventory Inv1;
    BookInventoryInfo[] currbooks;

    @Before
    public void setUp() throws Exception {
        Inv1 = createInventory();
        for(int i=0;i<5;i++){
            currbooks[i] = new BookInventoryInfo("book " + i, i+1, i+70);
        }
    }
    protected Inventory createInventory(){
        return new Inventory();
    }

    /**
     * We don't need tearDown function here.
     */
    /**
     * none
     */
    @Test
    public void getInstance() {
    }

    /**
     * @pre Inv1.books == null;
     * @post element in books exist iff this element exist in currbooks ;
     * for the test ONLY(!) we will assume that we have a field called inventory in Inventory.
     */
    @Test
    public void load() {
        assertTrue(Inv1.getInstance()!=null);
        if(Inv1.getInstance().inventory.length != currbooks.length) {
            fail("Arrays are not the same. ");
        }
        for(int i = 0; i<Inv1.getInstance().inventory.length;i++){
            if(Inv1.getInstance().inventory.getBookTitle() != currbooks[i].getBookTitle() || Inv1.getInstance().inventory.getAmountInInventory() != currbooks[i].getAmountInInventory() || Inv1.getInstance().inventory.getPrice() != currbooks[i].getPrice()){
                fail("Some elements are not the same. ");
            }
        }
    }

    /**
     * @pre Inv1.books != null;
     * @post: if take returns NOT_IN_STOCK then @pre(Inv.books.currAmount for all element) == @post(Inv.books.currAmount for all element)
     * else there exist only one element ,denote i, @pre( Inv.books[i].currAmount) != @post(Inv.books[i].currAmount)
     * this involves query and command.
     */
    @Test
    public void take() {
        assertTrue(Inv1.getInstance()!=null);
        OrderResult result = Inv1.take("book 1"); //Should return SUCCESSFULLY_TAKEN.
        if(result!= OrderResult.SUCCESSFULLY_TAKEN){
            fail("Take didn't find something that exist. ");
        }
        //we will check the the amount decrease by 1. it was 2.
        if(Inv1.getInstance().inventory[1].getAmountInInventory() != 1){
            fail("Take didn't change the amount. ");
        }
        result = Inv1.take("HarryBarry 1"); //Should return NOT_IN_STOCK.
        if(result!= OrderResult.NOT_IN_STOCK){
            fail("Take find something that doesn't exist. ");
        }
        //we will check that the amount didn't decrease. it was 2.
        if(Inv1.getInstance().inventory[1].getAmountInInventory() != 2){
            fail("Take change the amount and it was not suppose. ");
        }
    }

    /**
     * @pre none. Because if the inventory do not have this book then it should work and returns -1.
     * @post @pre(books) == @post(books).
     * this is a query.
     */
    @Test
    public void checkAvailabiltyAndGetPrice() {
        if(Inv1.checkAvailabiltyAndGetPrice("book 1") == -1){
            fail("checkAvailabiltyAndGetPrice didn't found something that exist. ");
        }
        if(Inv1.checkAvailabiltyAndGetPrice("book 1") != 71){
            fail("checkAvailabiltyAndGetPrice brought the wrong price. ");
        }
    }

    /**
     * none
     */
    @Test
    public void printInventoryToFile() {
    }
}