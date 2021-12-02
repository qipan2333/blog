# gitea

## main usage

* service for git repositories

## conceptions

* none

## practise

### pre-requirements

* none

### purpose

* create a kubernetes cluster by kind
* setup ingress-nginx
* install gitea

### do it

1. [create local cluster for testing](../basic/local.cluster.for.testing.md)
2. install ingress nginx
    * prepare [ingress.nginx.values.yaml](../basic/resources/ingress.nginx.values.yaml.md)
    * prepare images
        + ```shell
          for IMAGE in "k8s.gcr.io/ingress-nginx/controller:v1.0.3" "k8s.gcr.io/ingress-nginx/kube-webhook-certgen:v1.0"
          do
              LOCAL_IMAGE="localhost:5000/$IMAGE"
              docker image inspect $IMAGE || docker pull $IMAGE
              docker image tag $IMAGE $LOCAL_IMAGE
              docker push $LOCAL_IMAGE
          done
          ```
    * install with helm
        + ```shell
          ./bin/helm install \
              --create-namespace --namespace basic-components \
              my-ingress-nginx \
              ingress-nginx \
              --version 4.0.5 \
              --repo https://kubernetes.github.io/ingress-nginx \
              --values ingress.nginx.values.yaml \
              --atomic
          ```
3. install gitea
    * prepare [gitea.values.yaml](resources/gitea.values.yaml.md)
    * prepare images
        + ```shell
          for IMAGE in "gitea/gitea:1.15.3" \
              "bitnami/memcached:1.6.9-debian-10-r114" \
              "bitnami/memcached-exporter:0.8.0-debian-10-r105" \
              "bitnami/postgresql:11.11.0-debian-10-r62" \
              "bitnami/bitnami-shell:10" \
              "bitnami/postgres-exporter:0.9.0-debian-10-r34"
          do
              LOCAL_IMAGE="localhost:5000/$IMAGE"
              docker image inspect $IMAGE || docker pull $IMAGE
              docker image tag $IMAGE $LOCAL_IMAGE
              docker push $LOCAL_IMAGE
          done
          ```
    * create `gitea-admin-secret`
        + ```shell
          # uses the "Array" declaration
          # referencing the variable again with as $PASSWORD an index array is the same as ${PASSWORD[0]}
          ./bin/kubectl get namespace application \
              || ./bin/kubectl create namespace application
          PASSWORD=($((echo -n $RANDOM | md5sum 2>/dev/null) || (echo -n $RANDOM | md5 2>/dev/null)))
          ./bin/kubectl -n application \
              create secret generic gitea-admin-secret \
              --from-literal=username=admin \
              --from-literal=password=$PASSWORD
          ```
    * install with helm
        + ```shell
          ./bin/helm install \
              --create-namespace --namespace basic-components \
              my-gitea \
              gitea \
              --version 4.1.1 \
              --repo https://dl.gitea.io/charts \
              --values gitea.values.yaml \
              --atomic
          ```