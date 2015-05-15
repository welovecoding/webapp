package com.welovecoding.api.v1.account;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.welovecoding.IntegrationTestConfiguration;
import com.welovecoding.SchemaDumper;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigWebContextLoader.class, classes = {
  IntegrationTestConfiguration.class})
@WebAppConfiguration
@TestExecutionListeners({
  DependencyInjectionTestExecutionListener.class,
  DbUnitTestExecutionListener.class})
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AccountResourceIT {

  private static final Logger LOG = Logger.getLogger(AccountResourceIT.class.getName());

  @Rule
  public TestName name = new TestName();

  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Before
  public void setUp() {
    DatabaseDataSourceConnection datasource = (DatabaseDataSourceConnection) webApplicationContext.getBean("dbUnitDatabaseConnection");
    try {
      SchemaDumper.dumpSchema("testdb", datasource.getConnection());
    } catch (SQLException ex) {
      LOG.log(Level.SEVERE, null, ex);
    } catch (Exception ex) {
      LOG.log(Level.SEVERE, null, ex);
    }
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @After
  public void tearDown() {
  }

  @Test
  @DatabaseSetup(value = "insert.xml")
  public void testFindAccountByUsername() throws Exception {
    System.out.println(name.getMethodName());
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/account/username1"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().contentType("application/hal+json"))
      .andExpect(jsonPath("username", is("username" + 1)))
      .andReturn();

    String content = result.getResponse().getContentAsString();
  }

}
