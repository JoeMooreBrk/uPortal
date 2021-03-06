/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portal.events.aggr.login;

import org.jasig.portal.events.aggr.AggregationInterval;
import org.jasig.portal.events.aggr.BaseAggregationKeyImpl;
import org.jasig.portal.events.aggr.DateDimension;
import org.jasig.portal.events.aggr.TimeDimension;
import org.jasig.portal.events.aggr.groups.AggregatedGroupMapping;

final class LoginAggregationKeyImpl extends BaseAggregationKeyImpl implements LoginAggregationKey {
    private static final long serialVersionUID = 1L;

    public LoginAggregationKeyImpl(AggregationInterval aggregationInterval,
            AggregatedGroupMapping aggregatedGroupMapping) {
        super(aggregationInterval, aggregatedGroupMapping);
    }

    public LoginAggregationKeyImpl(LoginAggregation baseAggregation) {
        super(baseAggregation);
    }

    public LoginAggregationKeyImpl(DateDimension dateDimension, TimeDimension timeDimension,
            AggregationInterval aggregationInterval, AggregatedGroupMapping aggregatedGroupMapping) {
        super(dateDimension, timeDimension, aggregationInterval, aggregatedGroupMapping);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof LoginAggregationKey))
            return false;
        return super.equals(obj);
    }
    
    @Override
    public String toString() {
        return "LoginAggregationKey [dateDimension=" + getDateDimension() + ", timeDimension="
                + getTimeDimension() + ", interval=" + getInterval() + ", aggregatedGroup=" + getAggregatedGroup()
                + "]";
    }
}
