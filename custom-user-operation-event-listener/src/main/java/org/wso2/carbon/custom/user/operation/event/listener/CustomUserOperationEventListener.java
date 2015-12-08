package org.wso2.carbon.custom.user.operation.event.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.user.core.UserStoreManager;
import org.wso2.carbon.user.core.common.AbstractUserOperationEventListener;

import java.util.HashMap;
import java.util.Map;

public class CustomUserOperationEventListener extends AbstractUserOperationEventListener {

    private static Log log = LogFactory.getLog(CustomUserOperationEventListener.class);

    private static final String LAST_LOGIN_TIMESTAMP_CLAIM = "http://wso2.org/claims/lastLoginTimestamp";

    @Override
    public int getExecutionOrderId() {

        //This listener should execute before the IdentityMgtEventListener
        //Hence the number should be < 1357 (Execution order ID of IdentityMgtEventListener)
        return 1356;
    }

    @Override
    public boolean doPostAuthenticate(String userName, boolean authenticated, UserStoreManager userStoreManager)
            throws UserStoreException {

        if (authenticated) {
            // User is successfully authenticated

            if (!userStoreManager.isReadOnly()) {

                // The userstore is read/write. Persist the current timestamp as the last login timestamp claim

                long lastLoginTimestamp = System.currentTimeMillis();

                Map<String, String> claimMap = new HashMap<String, String>();

                claimMap.put(LAST_LOGIN_TIMESTAMP_CLAIM, Long.toString(lastLoginTimestamp));

                userStoreManager.setUserClaimValues(userName, claimMap, null);

                if (log.isDebugEnabled()) {
                    log.debug("Last Login Timestamp for user : " + userName + " is " + String.valueOf(lastLoginTimestamp));
                }
            }
        }

        return true;
    }

}
