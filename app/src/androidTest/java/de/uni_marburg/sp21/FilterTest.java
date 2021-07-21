package de.uni_marburg.sp21;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.DrawerMatchers;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni_marburg.sp21.company_data_structure.Address;
import de.uni_marburg.sp21.company_data_structure.Category;
import de.uni_marburg.sp21.company_data_structure.Company;
import de.uni_marburg.sp21.company_data_structure.Message;
import de.uni_marburg.sp21.company_data_structure.Organization;
import de.uni_marburg.sp21.company_data_structure.ProductGroup;
import de.uni_marburg.sp21.company_data_structure.Restriction;
import de.uni_marburg.sp21.company_data_structure.ShopType;
import de.uni_marburg.sp21.filter.CheckItem;
import de.uni_marburg.sp21.filter.Filter;
import de.uni_marburg.sp21.filter.PickedTime;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.util.Checks.checkNotNull;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import androidx.test.espresso.contrib.RecyclerViewActions;
/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class FilterTest {
    List<Company> companies = new ArrayList<>();
    CheckItem[] type;
    CheckItem[] organizations;
    CheckItem[] restrictions;
     CheckItem[] categories;

    @Mock
    Context mMockContext;


    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);


    @Before
    public void setUp(){

        type = ShopType.createCheckItemArray();
        categories = Category.createCheckItemArray();
        organizations = new CheckItem[]{new CheckItem("Blue Organization")};
        restrictions = Restriction.createCheckItemArray();



        Address address1 = new Address("Marburg", "Auf der Höhe 2A","35041");
        Address address2 = new Address("Gladenbach", "Freiherr-vom-Stein-Straße 28","35075");
        Address address3 = new Address("Battenberg", "Dorfstrsße 1","35088");
        DataBaseManager dataBaseManager = new DataBaseManager();
        //FirebaseFirestore database = FirebaseFirestore.getInstance();

        //List<Company> companies = DataBaseManager.getCompanyList(database);

        Message message1 = new Message("24-02-2020", "Its good", "companyName");
        Message message2 = new Message("25-03-2020", "Its fine" , "companyName");
        Message message3 = new Message("26-04-2020", "Its bad" , "companyName");
        List<Message> messageList = new ArrayList<>();
        messageList.add(message1);
        messageList.add(message3);


        Map<String, ArrayList<Map<String, String>>> openingHours1 = new HashMap<>();
        ArrayList<Map<String,String>> opened = new ArrayList<>();
        Map<String, String> hours = new HashMap<>();
        Map<String, String> hours2 = new HashMap<>();
        hours.put("start", "10:00");
        hours.put("end", "14:00");
        hours2.put("start", "15:00");
        hours2.put("end", "20:00");
        opened.add(hours);
        opened.add(hours2);

        openingHours1.put("monday", opened);
        openingHours1.put("tuesday", opened);
        openingHours1.put("wednesday", opened);
        openingHours1.put("thursday", opened);
        openingHours1.put("friday", opened);
        openingHours1.put("saturday", opened);
        openingHours1.put("sunday", null);

        Company company1 =  new Company("1");
        Company company2 =  new Company("2");
        Company company3 =  new Company("3");
        company1.setDeliveryService(false);
        company1.setAddress(address1);
        company1.setMessages(messageList);
        company1.setMail("lenny.daum@web.de");
        company1.setDescription("Something to test with");
        company1.setName("Rosenbauer");
        company1.setOwner("Baronin Lisann");
        company1.setOpeningHours(openingHours1);
        company1.setProductsDescription("Its a test description");
        company1.setDescription("Its an another test description ");
        company1.setOpeningHoursComments("Its a test for opening hours comment");


        List<String> types = new ArrayList<>();
        types.add(type[2].toString());
        types.add(type[3].toString());
        company1.setTypes(types);

        List<Organization> organizationList = new ArrayList<>();
        organizationList.add(new Organization(1.0, "Blue Organization", "https://exampleURL.com"));
        company1.setOrganizations(organizationList);

        List<ProductGroup> productGroups = new ArrayList<>();
        ProductGroup productGroup = new ProductGroup(Category.MEAT, 1.0);
        productGroup.setRawProd(false);
        List<String> tags = new ArrayList<>();
        tags.add(Category.MEAT.toString());
        productGroup.setProductTags(tags);
        productGroups.add(productGroup);

       // URL a = ClassLoader.getSystemResource("string");
//        a.get

        company1.setProductGroups(productGroups);
        companies.add(company1);


    }
    @Test
    public void testFoo() {

        // .. test project environment
        Context testContext = getInstrumentation().getContext();
        Resources testRes = testContext.getResources();

       // InputStream ts = testRes.openRawResource(R.raw.your_res);

       // System.out.println(testRes.getString(R.string.address));
        assertNotNull(testRes);
    }
    @Test
    public void favoriteTest(){
        // mark a company as a favorite
        onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public void perform(UiController uiController, View view) {
                view.findViewById(R.id.favorite).performClick();
            }
        }));

        // clicks the favorite button and shows all favorites company
        onView(withId(R.id.favorite_button)).perform(click());

        onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.scrollToPosition(0)).check(matches(isDisplayed()));
    }

    @Test
    public void onViewTest() {

        // FilterButton and BottomSheetDialog
        onView(withId(R.id.filterButton)).perform(click());
        onView(withId(R.id.bottom_sheet)).check(matches(isDisplayed()));

        onView(withId(R.id.categoryRV)).perform(click());

    }
    @Test
    public void searchFilterTest(){
        List<Company> searchFilter = Filter.filter("Rosen", companies, type, organizations, categories, restrictions, false, false,  new PickedTime());
        assertEquals(companies,searchFilter);

        List<Company> multipleSearchFilter = Filter.filter("Rosen/marburg", companies, type, organizations, categories, restrictions, false, false,  new PickedTime());
        assertEquals(companies,multipleSearchFilter);
    }
    @Test
    public void categoriesFilterTest(){
        List<Company> emptyList = new ArrayList<>();

        categories[0].check(true);
        List<Company> categoryFilter1 = Filter.filter("", companies, type, organizations, categories, restrictions, false, false, new PickedTime());
        assertEquals(emptyList, categoryFilter1);
        categories[0].check(false);

        categories[1].check(true);
        List<Company> categoryFilter2 = Filter.filter("", companies, type, organizations, categories, restrictions, false, false, new PickedTime());
        assertEquals(emptyList, categoryFilter2);
        categories[1].check(false);

        categories[2].check(true);
        List<Company> categoryFilter3 = Filter.filter("", companies, type, organizations, categories, restrictions, false, false, new PickedTime());
        assertEquals(companies, categoryFilter3);
        categories[2].check(false);

        categories[3].check(true);
        List<Company> categoryFilter4 = Filter.filter("", companies, type, organizations, categories, restrictions, false, false, new PickedTime());
        assertEquals(emptyList, categoryFilter4);
        categories[3].check(false);

        categories[4].check(true);
        List<Company> categoryFilter5 = Filter.filter("", companies, type, organizations, categories, restrictions, false, false, new PickedTime());
        assertEquals(emptyList, categoryFilter5);
        categories[4].check(false);

        categories[5].check(true);
        List<Company> categoryFilter6 = Filter.filter("", companies, type, organizations, categories, restrictions, false, false, new PickedTime());
        assertEquals(emptyList, categoryFilter6);
        categories[5].check(false);

        categories[6].check(true);
        List<Company> categoryFilter7 = Filter.filter("", companies, type, organizations, categories, restrictions, false, false, new PickedTime());
        assertEquals(emptyList, categoryFilter7);
        categories[6].check(false);

        categories[7].check(true);
        List<Company> categoryFilter8 = Filter.filter("", companies, type, organizations, categories, restrictions, false, false, new PickedTime());
        assertEquals(emptyList, categoryFilter8);
        categories[7].check(false);

        categories[8].check(true);
        List<Company> categoryFilter9 = Filter.filter("", companies, type, organizations, categories, restrictions, false, false, new PickedTime());
        assertEquals(emptyList, categoryFilter9);
        categories[8].check(false);

        categories[9].check(true);
        List<Company> categoryFilter10 = Filter.filter("", companies, type, organizations, categories, restrictions, false, false, new PickedTime());
        assertEquals(emptyList, categoryFilter10);
        categories[9].check(false);

        categories[10].check(true);
        List<Company> categoryFilter11 = Filter.filter("", companies, type, organizations, categories, restrictions, false, false, new PickedTime());
        assertEquals(emptyList, categoryFilter11);
        categories[10].check(false);

        categories[11].check(true);
        List<Company> categoryFilter12 = Filter.filter("", companies, type, organizations, categories, restrictions, false, false, new PickedTime());
        assertEquals(emptyList, categoryFilter12);
        categories[11].check(false);
    }
    @Test
    public void typesFilterTest(){
        List<Company> emptyList = new ArrayList<Company>();

        type[2].check(true);
        List<Company> typeFilter1 = Filter.filter("", companies, type, organizations, categories, restrictions, false, false,  new PickedTime());
        //assertEquals(companies, typeFilter1);
        type[2].check(false);

        type[0].check(true);
        List<Company> typeFilter2 = Filter.filter("", companies, type, organizations, categories, restrictions, false, false,  new PickedTime());
        assertEquals(emptyList, typeFilter2);
        type[0].check(false);

        type[1].check(true);
        List<Company> typeFilter3 = Filter.filter("", companies, type, organizations, categories, restrictions, false, false,  new PickedTime());
        //assertEquals(emptyList, typeFilter3);
        type[1].check(false);

        type[3].check(true);
        List<Company> typeFilter4 = Filter.filter("", companies, type, organizations, categories, restrictions, false, false,  new PickedTime());
        //assertEquals(companies, typeFilter4);
        type[3].check(false);

        type[4].check(true);
        List<Company> typeFilter5 = Filter.filter("", companies, type, organizations, categories, restrictions, false, false,  new PickedTime());
        assertEquals(emptyList, typeFilter5);
        type[4].check(false);
    }
    @Test
    public void organizationsFilterTest(){
        organizations[0].check(true);
        List<Company> organizationFilter = Filter.filter("", companies, type, organizations, categories, restrictions, false, false, new PickedTime());
        assertEquals(companies, organizationFilter);
        organizations[0].check(false);
    }

    @Test
    public void restrictionsFilterTest(){
        restrictions[1].check(true);
        List<Company> restrictionFilter1 = Filter.filter("Baronin", companies, type, organizations, categories, restrictions, false, false, new PickedTime());
        assertEquals(companies, restrictionFilter1);
        restrictions[1].check(false);

        restrictions[0].check(true);
        List<Company> restrictionFilter2 = Filter.filter("Rosen", companies, type, organizations, categories, restrictions, false, false, new PickedTime());
        assertEquals(companies, restrictionFilter2);
        restrictions[0].check(false);

        restrictions[2].check(true);
        List<Company> restrictionFilter3 = Filter.filter("Hotel", companies, type, organizations, categories, restrictions, false, false, new PickedTime());
        assertEquals(companies, restrictionFilter3);
        restrictions[2].check(false);

        restrictions[3].check(true);
        List<Company> restrictionFilter4 = Filter.filter("Marburg", companies, type, organizations, categories, restrictions, false, false, new PickedTime());
        assertEquals(companies, restrictionFilter4);
        restrictions[3].check(false);

        restrictions[4].check(true);
        List<Company> restrictionFilter5 = Filter.filter("Its an another test description", companies, type, organizations, categories, restrictions, false, false, new PickedTime());
        assertEquals(companies, restrictionFilter5);
        restrictions[4].check(false);

        restrictions[5].check(true);
        List<Company> restrictionFilter6 = Filter.filter("Its a test description", companies, type, organizations, categories, restrictions, false, false, new PickedTime());
        assertEquals(companies, restrictionFilter6);
        restrictions[5].check(false);

        restrictions[6].check(true);
        List<Company> restrictionFilter7 = Filter.filter(Category.MEAT.toString(), companies, type, organizations, categories, restrictions, false, false, new PickedTime());
        assertEquals(companies, restrictionFilter7);
        restrictions[6].check(false);

        restrictions[7].check(true);
        List<Company> restrictionFilter8 = Filter.filter("Its a test for opening hours comment", companies, type, organizations, categories, restrictions, false, false, new PickedTime());
        assertEquals(companies, restrictionFilter8);
        restrictions[7].check(false);

        restrictions[8].check(true);
        List<Company> restrictionFilter9 = Filter.filter("Blue Organization", companies, type, organizations, categories, restrictions, false, false, new PickedTime());
        assertEquals(companies, restrictionFilter9);
        restrictions[8].check(false);

        restrictions[9].check(true);
        List<Company> restrictionFilter10 = Filter.filter("Its good", companies, type, organizations, categories, restrictions, false, false, new PickedTime());
        assertEquals(companies, restrictionFilter10);
        restrictions[9].check(false);
    }
    @Test
    public void isOpenFilterTest() throws ParseException {
        PickedTime pickedTime = new PickedTime();
        pickedTime.setWeekday(TimeConverter.convertToDatabaseWeekday("monday"));

        pickedTime.setStartTime(TimeConverter.convertToDate("10:05"));
        pickedTime.setEndTime(TimeConverter.convertToDate("11:00"));
        List<Company> isOpn = Filter.filter("", companies, type, organizations, categories, restrictions, false, true, pickedTime);
        assertEquals(companies, isOpn);
    }

     @Test
    public void isDeliveryFilterTest(){
         List<Company> isDelivery = Filter.filter("", companies, type, organizations, categories, restrictions, true, false, new PickedTime());
         List<Company> empty = new ArrayList<>();
         assertEquals(empty, isDelivery);
    }
    @Test
    public void timerFilterTest(){
        PickedTime testTime = new PickedTime();
        testTime.setWeekday("monday");
        testTime.setStartTime(TimeConverter.convertToDate("10:00"));
        testTime.setEndTime(TimeConverter.convertToDate("11:00"));
        List<Company> timeFilter = Filter.filter("", companies, type, organizations, categories, restrictions, false, false, testTime);
        assertEquals(companies, timeFilter);

        PickedTime testTime2 = new PickedTime();
        testTime2.setWeekday("sunday");
        testTime2.setStartTime(TimeConverter.convertToDate("10:00"));
        testTime2.setEndTime(TimeConverter.convertToDate("14:00"));
        List<Company> empty = new ArrayList<>();
        List<Company> timeFilter2 = Filter.filter("", companies, type, organizations, categories, restrictions, false, false,testTime2);
        assertEquals(empty, timeFilter2);

    }
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = getInstrumentation().getTargetContext();
        assertEquals("de.uni_marburg.sp21", appContext.getPackageName());
    }
}