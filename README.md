# OpenShift web console
http://manage.openshift.com/

# OpenShift oc
oc login https://console.starter-us-west-2.openshift.com -u Voyager4DK 
--insecure-skip-tls-verify=true

# Git url
https://github.com/Voyager4DK/hc-loot.git
check this out: https://github.com/jboss-openshift/openshift-quickstarts

# Deploy to OS
mvn package fabric8:deploy -Popenshift -DskipTests

oc rollout status dc/hc-loot

# Deploy db
oc new-app -e POSTGRESQL_USER=luke -e POSTGRESQL_PASSWORD=secret -e POSTGRESQL_DATABASE=my_data openshift/postgresql-92-centos7 --name=my-database

# Connect to db
psql postgresql://${MY_DATABASE_SERVICE_HOST}:${MY_DATABASE_SERVICE_PORT}/my_data -U luke -c "SELECT * FROM player"

#Url:
https://bit.ly/2BcYDYs
http://hc-loot-voyager4dk.7e14.starter-us-west-2.openshiftapps.com/