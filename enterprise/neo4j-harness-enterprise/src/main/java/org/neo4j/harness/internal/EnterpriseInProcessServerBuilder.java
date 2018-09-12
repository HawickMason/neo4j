/*
 * Copyright (c) 2002-2018 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j Enterprise Edition. The included source
 * code can be redistributed and/or modified under the terms of the
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3
 * (http://www.fsf.org/licensing/licenses/agpl-3.0.html) with the
 * Commons Clause, as found in the associated LICENSE.txt file.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * Neo4j object code can be licensed independently from the source
 * under separate terms from the AGPL. Inquiries can be directed to:
 * licensing@neo4j.com
 *
 * More information is also available at:
 * https://neo4j.com/licensing/
 */
package org.neo4j.harness.internal;

import java.io.File;

import org.neo4j.causalclustering.core.CausalClusteringSettings;
import org.neo4j.causalclustering.discovery.DiscoveryServiceFactorySelector;
import org.neo4j.graphdb.facade.GraphDatabaseFacadeFactory.Dependencies;
import org.neo4j.kernel.configuration.Config;
import org.neo4j.logging.LogProvider;
import org.neo4j.server.AbstractNeoServer;
import org.neo4j.server.database.EnterpriseGraphFactory;
import org.neo4j.server.database.GraphFactory;
import org.neo4j.server.enterprise.OpenEnterpriseNeoServer;

public class EnterpriseInProcessServerBuilder extends AbstractInProcessServerBuilder
{
    private DiscoveryServiceFactorySelector.DiscoveryImplementation discoveryServiceFactory = DiscoveryServiceFactorySelector.DEFAULT;

    public EnterpriseInProcessServerBuilder()
    {
        this( new File( System.getProperty( "java.io.tmpdir" ) ) );
    }

    public EnterpriseInProcessServerBuilder( File workingDir )
    {
        super( workingDir );
    }

    public EnterpriseInProcessServerBuilder( File workingDir, String dataSubDir )
    {
        super( workingDir, dataSubDir );
    }

    @Override
    protected GraphFactory createGraphFactory( Config config )
    {
        config.augment( CausalClusteringSettings.discovery_implementation, discoveryServiceFactory.name() );
        return new EnterpriseGraphFactory();
    }

    @Override
    protected AbstractNeoServer createNeoServer( GraphFactory graphFactory, Config config, Dependencies dependencies, LogProvider userLogProvider )
    {
        return new OpenEnterpriseNeoServer( config, graphFactory, dependencies, userLogProvider );
    }

    /**
     * Configure the server to use the specified service to build cluster topologies and share associated metadata.
     *
     * Only relevant for causal clustering.
     *
     * @param discoveryService
     * @return this builder instance
     */
    public EnterpriseInProcessServerBuilder withDiscoveryServiceFactory( DiscoveryServiceFactorySelector.DiscoveryImplementation discoveryService )
    {
        this.discoveryServiceFactory = discoveryService;
        return this;
    }

}
