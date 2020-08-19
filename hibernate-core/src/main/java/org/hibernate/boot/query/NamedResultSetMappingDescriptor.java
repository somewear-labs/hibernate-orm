/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.boot.query;

import org.hibernate.query.internal.ResultSetMappingResolutionContext;
import org.hibernate.query.named.NamedResultSetMappingMemento;
import org.hibernate.query.spi.QueryEngine;

/**
 * Models the "boot view" of a ResultSet mapping used in the mapping
 * of native and procedure queries.
 *
 * Ultimately used to generate a NamedResultSetMappingMemento that is
 * stored in the {@link org.hibernate.query.named.NamedQueryRepository}
 * for availability at runtime
 *
 * @author Steve Ebersole
 */
public interface NamedResultSetMappingDescriptor {
	/**
	 * The name under which the result-set-mapping is to be registered
	 */
	String getRegistrationName();

	/**
	 * Create a representation of the described ResultSet mapping for the purpose of
	 * being stored in Hibernate's {@link org.hibernate.query.named.NamedQueryRepository}
	 *
	 * @see QueryEngine#getNamedQueryRepository()
	 */
	NamedResultSetMappingMemento resolve(ResultSetMappingResolutionContext resolutionContext);
}
