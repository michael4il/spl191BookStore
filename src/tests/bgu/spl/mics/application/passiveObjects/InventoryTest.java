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

    @After
    public void tearDown() throws Exception {
    }

    /*none*/
    @Test
    public void getInstance() {
    }

    /**
     * @pre Inv1.books == null;
     * @post element in books exist iff this element exist in currbooks ;
     */
    @Test
    public void load() {
        assertTrue(Inv1.isEmpty());
        Inv1.load(books);
        //TODO
        for{

        }
    }

    /**
     * @pre Inv1.books != null;
     * @post: if take returns NOT_IN_STOCK then @pre(Inv.books.currAmount for all element) == @post(Inv.books.currAmount for all element)
     * else there exist only one element ,denote i, @pre( Inv.books[i].currAmount) != @post(Inv.books[i].currAmount)
     */
    @Test
    public void take() {

    }

    /**
     * @pre none. Because if the inventory is empty then the should work and returns -1.
     * @post none.
     */
    @Test
    public void checkAvailabiltyAndGetPrice() {
    }

    @Test
    public void printInventoryToFile() {
    }
}