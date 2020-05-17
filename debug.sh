mvn clean package

OVEROPS_URL=http://localhost:8080
OVEROPS_API_KEY=Kjibm5PK1D4hLfseWkpvZ/Y7BmqpZwJFwnbT5gso
OVEROPS_SID=S1
OVEROPS_APPLICATION_NAME=event-generator
OVEROPS_DEPLOYMENT_NAME=deployment1
OVEROPS_NEW_EVENTS=true

java -jar target/gitlab-overops.jar \
    ${OVEROPS_URL:+--overOpsURL=$OVEROPS_URL} \
    ${OVEROPS_SID:+--overOpsSID=$OVEROPS_SID} \
    ${OVEROPS_API_KEY:+--overOpsAPIKey=$OVEROPS_API_KEY} \
    ${OVEROPS_APPLICATION_NAME:+--applicationName=$OVEROPS_APPLICATION_NAME} \
    ${OVEROPS_DEPLOYMENT_NAME:+--deploymentName=$OVEROPS_DEPLOYMENT_NAME} \
    ${OVEROPS_SERVICE_ID:+--serviceId=$OVEROPS_SERVICE_ID} \
    ${OVEROPS_REGEX_FILTER:+--regexFilter=$OVEROPS_REGEX_FILTER} \
    ${OVEROPS_MARK_UNSTABLE:+--markUnstable=$OVEROPS_MARK_UNSTABLE} \
    ${OVEROPS_PRINT_TOP_ISSUES:+--printTopIssues=$OVEROPS_PRINT_TOP_ISSUES} \
    ${OVEROPS_NEW_EVENTS:+--newEvents=$OVEROPS_NEW_EVENTS} \
    ${OVEROPS_RESURFACED_ERRORS:+--resurfacedErrors=$OVEROPS_RESURFACED_ERRORS} \
    ${OVEROPS_MAX_ERROR_VOLUME:+--maxErrorVolume=$OVEROPS_MAX_ERROR_VOLUME} \
    ${OVEROPS_MAX_UNIQUE_ERRORS:+--maxUniqueErrors=$OVEROPS_MAX_UNIQUE_ERRORS} \
    ${OVEROPS_CRITICAL_EXCEPTION_TYPES:+--criticalExceptionTypes=$OVEROPS_CRITICAL_EXCEPTION_TYPES} \
    ${OVEROPS_ACTIVE_TIMESPAN:+--activeTimespan=$OVEROPS_ACTIVE_TIMESPAN} \
    ${OVEROPS_BASE_LINE_TIMESPAN:+--baselineTimespan=$OVEROPS_BASE_LINE_TIMESPAN} \
    ${OVEROPS_MIN_VOLUME_THRESHOLD:+--minVolumeThreshold=$OVEROPS_MIN_VOLUME_THRESHOLD} \
    ${OVEROPS_MIN_ERROR_RATE_THRESHOLD:+--minErrorRateThreshold=$OVEROPS_MIN_ERROR_RATE_THRESHOLD} \
    ${OVEROPS_APPLY_SEASONALITY:+--applySeasonality=$OVEROPS_APPLY_SEASONALITY} \
    ${OVEROPS_SHOW_EVENTS_FOR_PASSED_GATES:+--showEventsForPassedGates=$OVEROPS_SHOW_EVENTS_FOR_PASSED_GATES} \
    ${OVEROPS_DEBUG:+--debug=$OVEROPS_DEBUG} \
    ${OVEROPS_CHECK:+--checkVersion=$OVEROPS_CHECK}
open /tmp/quality-report.html