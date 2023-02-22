/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.hql;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.IntStream;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.hql.internal.QuerySplitter;
import org.hibernate.metamodel.internal.MetamodelImpl;

import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Steve Ebersole
 */
public class QuerySplitterTest extends BaseNonConfigCoreFunctionalTestCase {
	@Test
	public void testQueryWithEntityNameAsStringLiteral() {
		final String qry = "select e from Employee a where e.name = ', Employee Number 1'";

		String[] results = QuerySplitter.concreteQueries( qry, sessionFactory() );
		assertEquals( 1, results.length );
		assertEquals(
				"select e from org.hibernate.test.hql.QuerySplitterTest$Employee a where e.name = ', Employee Number 1'",
				results[0]
		);
	}

	@Test
	@TestForIssue(jiraKey = "HHH-7973" )
	public void testQueryWithEntityNameAsStringLiteral2() {
		final String qry = "from Employee where name = 'He is the, Employee Number 1'";

		String[] results = QuerySplitter.concreteQueries( qry, sessionFactory() );
		assertEquals( 1, results.length );
		assertEquals(
				"from org.hibernate.test.hql.QuerySplitterTest$Employee where name = 'He is the, Employee Number 1'",
				results[0]
		);
	}

	@Test
	@TestForIssue(jiraKey = "HHH-14948")
	public void testMemoryConsumptionOfFailedImportsCache() throws NoSuchFieldException, IllegalAccessException {

		IntStream.range( 0, 1001 )
				.forEach( i -> QuerySplitter.concreteQueries(
						"from Employee e join e.company" + i,
						sessionFactory()
				) );

		Map<String, String> validImports = extractMapFromMetamodel("knownValidImports");
		Map<String, String> invalidImports  = extractMapFromMetamodel("knownInvalidImports");

		assertEquals( 2, validImports.size() );

		// VERY hard-coded, but considering the possibility of a regression of a memory-related issue,
		// it should be worth it
		assertEquals( 1_000, invalidImports.size() );
	}

	private Map<String, String> extractMapFromMetamodel(String fieldName) throws NoSuchFieldException, IllegalAccessException {
		MetamodelImpl metamodel = (MetamodelImpl) sessionFactory().getMetamodel();
		Field field = MetamodelImpl.class.getDeclaredField( fieldName );
		field.setAccessible( true );
		//noinspection unchecked
		return (Map<String, String>) field.get( metamodel );
	}

	@Test
	@TestForIssue(jiraKey = "HHH-7973" )
	public void testQueryWithEntityNameAsStringLiteralAndEscapeQuoteChar() {
		final String qry = "from Employee where name = '''He is '' the, Employee Number 1'''";

		String[] results = QuerySplitter.concreteQueries( qry, sessionFactory() );
		assertEquals( 1, results.length );
		assertEquals(
				"from org.hibernate.test.hql.QuerySplitterTest$Employee where name = '''He is '' the, Employee Number 1'''",
				results[0]
		);
	}

	@Override
	protected Class[] getAnnotatedClasses() {
		return new Class[] { Employee.class };
	}

	@Entity( name = "Employee" )
	@Table( name= "tabEmployees" )
	public class Employee {
		@Id
		private long id;
		private String name;

		public Employee() {

		}

		public Employee(long id, String strName) {
			this();
			this.name = strName;
		}

		public long getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public void setName(String strName) {
			this.name = strName;
		}

	}
}
