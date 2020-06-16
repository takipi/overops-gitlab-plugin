[![Build Status](https://jenkins-k8s.overops-samples.com/buildStatus/icon?job=overops-gitlab-plugin)](https://jenkins-k8s.overops-samples.com/job/overops-gitlab-plugin/)
# GitLab Overops Plugin
## Quick Set Up
Assuming there is already an instance of GitLab up, the next step is to attach the OverOps GitLab Plugin to your existing pipeline.

## Configure Runner
See GitLab Runner Quickstart Wiki Documentation

## Configuration example

Example file name: `.gitlab-ci.yml`

```yaml
image: ccaspanello/overops-gitlab-plugin:latest

variables:
  TAKIPI_BASE_URL: https://backend.overops.com
  TAKIPI_LISTEN_PORT: 6060
  TAKIPI_SECRET_KEY: ${TAKIPI_SECRET_KEY}
  OVEROPS_API_KEY: ${OVEROPS_API_KEY}
  OVEROPS_SID: ${VEROPS_SID}
  OVEROPS_URL: ${OVEROPS_URL}


  OVEROPS_APPLICATION_NAME: ${CI_PROJECT_NAME}
  OVEROPS_DEPLOYMENT_NAME: job-${CI_COMMIT_SHORT_SHA}
  OVEROPS_MARK_UNSTABLE: "true"
  OVEROPS_NEW_EVENTS: "true"
  OVEROPS_RESURFACED_ERRORS: "true"
  OVEROPS_MAX_ERROR_VOLUME: 5
  OVEROPS_MAX_UNIQUE_ERRORS: 5
  OVEROPS_CRITICAL_EXCEPTION_TYPES: "ExampleUncaughtException, SAXParseException"
  # OVEROPS_REGEX_FILTER:
  OVEROPS_PRINT_TOP_ISSUES: 5
  OVEROPS_ACTIVE_TIMESPAN: "0m"
  OVEROPS_BASE_LINE_TIMESPAN: "1d"
  OVEROPS_MIN_VOLUME_THRESHOLD: 1
  OVEROPS_MIN_ERROR_RATE_THRESHOLD: "0.0001"
  OVEROPS_APPLY_SEASONALITY: "true"
  OVEROPS_SHOW_EVENTS_FOR_PASSED_GATES: "true"
  OVEROPS_DEBUG: "true"

test:
 stage: test
 services:
   - overops/collector:4.48.0
 script:
   # - wget -q --content-disposition https://app.overops.com/app/download?t=sa-tgz
   # - tar xvzf takipi-agent-*.tar.gz
   - tar xvzf takipi-agent-4.50.0.tar.gz

   - echo takipi.deployment.name=job-${CI_COMMIT_SHORT_SHA} > takipi/agent.properties
   - echo takipi.application.name=${CI_PROJECT_NAME} >> takipi/agent.properties
   - echo takipi.collector.host=overops-collector >> takipi/agent.properties
   - echo takipi.collector.port=${TAKIPI_LISTEN_PORT} >> takipi/agent.properties
   - echo shutdownGraceTime=20000 >> takipi/agent.properties

 
   - ./mvnw -DargLine=-agentpath:./takipi/lib/libTakipiAgent.so test

overops:
  stage: test
  when: on_success
  allow_failure: false
  artifacts:
    name: "OverOps Quality Report $CI_JOB_ID"
    when: always
    paths:
      - quality-report.html
    untracked: true
    expire_in: 90 days
  script:
   - /opt/run-quality-report.sh
```

## Configuration parameters
Parameter | Required | Default Value | Description
---------|----------|---------|---------

OVEROPS_APP_URL | true | --- | The OverOps Backend URL(Saas: https://app.overops.com)
OVEROPS_URL | true | --- | The OverOps API Endpoint(Saas: https://api.overops.com)
OVEROPS_SID | true | --- | The OverOps environment identifier (e.g S4567) to inspect data for this build
OVEROPS_API_KEY | true | --- | API Key for interaction with OverOps API
OVEROPS_APPLICATION_NAME | false | --- | Use this parameter if the application name will be static. [Application Name](https://doc.overops.com/docs/naming-your-application-server-deployment) as specified in OverOps
OVEROPS_DEPLOYMENT_NAME  | false | --- | Use this parameter if the deployement_name will be static. [Deployment Name](https://doc.overops.com/docs/naming-your-application-server-deployment) as specified in OverOps
OVEROPS_REGEX_FILTER     | false | | A way to filter out specific event types from affecting the outcome of the OverOps Reliability report.
OVEROPS_MARK_UNSTABLE    | false | false | If set to `true` the build will be failed if any of the above gates are met
OVEROPS_PRINT_TOP_ISSUES  | false | 5 | Prints the top X events (as provided by this parameter) with the highest volume of errors detected within the active time window, This is useful when used in conjunction with Max Error Volume to identify the errors which caused a build to fail
OVEROPS_NEW_EVENTS       | false | false | If any new errors is detected, the build will be marked as failed
OVEROPS_RESURFACED_ERRORS| false | false | If any resurfaced errors is detected, the build will be marked as failed
OVEROPS_MAX_ERROR_VOLUME  | false | 0     | Set the max total error volume allowed. If exceeded the build will be marked as failed
OVEROPS_MAX_UNIQUE_ERRORS | false | 0     | Set the max total error volume allowed. If exceeded the build will be marked as failed
OVEROPS_CRITICAL_EXCEPTION_TYPES | false | | A comma delimited list of exception types that are deemed as severe regardless of their volume.<br>- If any events of any exceptions listed have a count greater than zero, the build will be marked as unstable. Blank to skip this test.<br>*(For example: `NullPointerException,IndexOutOfBoundsException`)*
OVEROPS_SHOW_EVENTS_FOR_PASSED_GATES | false | false | Display events for the quality gates even if the the gates passed.
pass_build_on_exception | false | false | Determines if the build should pass if there are exception/exceptions.
OVEROPS_DEBUG | false | false | For advanced debugging purposes only
OVEROPS_LINK | false | false | Set true if you want to simply generate a link to the report to view later
