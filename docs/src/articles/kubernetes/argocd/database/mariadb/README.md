# mariadb

## prepare

1. k8s is ready
2. argocd is ready and logged in
3. cert-manager is ready and the clusterissuer named `self-signed-ca-issuer` is ready
    * only required by `phpmyadmin` in the tests

## installation

1. prepare `mariadb.yaml`
    * ```yaml
      <!-- @include: mariadb.yaml -->
      ```
2. prepare credentials secret
    * ```shell
      kubectl get namespaces database > /dev/null 2>&1 || kubectl create namespace database
      kubectl -n database create secret generic mariadb-credentials \
          --from-literal=mariadb-root-password=$(tr -dc A-Za-z0-9 </dev/urandom | head -c 16) \
          --from-literal=mariadb-replication-password=$(tr -dc A-Za-z0-9 </dev/urandom | head -c 16) \
          --from-literal=mariadb-password=$(tr -dc A-Za-z0-9 </dev/urandom | head -c 16)
      ```
3. apply to k8s
    * ```shell
      kubectl -n argocd apply -f mariadb.yaml
      ```
4. sync by argocd
    * ```shell
      argocd app sync argocd/mariadb
      ```
5. expose interface
    1. prepare `mariadb-expose.yaml`
        * ```yaml
          <!-- @include: mariadb-expose.yaml -->
          ```
    2. apply to k8s
        * ```shell
          kubectl -n database apply -f mariadb-expose.yaml
          ```

## tests with cli

1. with root user
    * ```shell
      ROOT_PASSWORD=$(kubectl -n database get secret mariadb-credentials -o jsonpath='{.data.mariadb-root-password}' | base64 -d)
      podman run --rm \
          -e MYSQL_PWD=${ROOT_PASSWORD} \
          -it docker.io/library/mariadb:11.2.2-jammy \
          mariadb \
          --host host.containers.internal \
          --port 32306 \
          --user root \
          --database mysql \
          --execute 'show databases'
      ```
2. with normal user
    * ```shell
      PASSWORD=$(kubectl -n database get secret mariadb-credentials -o jsonpath='{.data.mariadb-password}' | base64 -d)
      podman run --rm \
          -e MYSQL_PWD=${PASSWORD} \
          -it docker.io/library/mariadb:11.2.2-jammy \
          mariadb \
          --host host.containers.internal \
          --port 32306 \
          --user ben.wangz \
          --database geekcity \
          --execute 'show databases'
      ```

## test with phpmyadmin

1. prepare `phpmyadmin.yaml`
    * ```yaml
      <!-- @include: phpmyadmin.yaml -->
      ```
2. apply to k8s
    * ```shell
      kubectl -n argocd apply -f phpmyadmin.yaml
      ```
3. sync by argocd
    * ```shell
      argocd app sync argocd/phpmyadmin
      ```
4. open with browser: https://phpmyadmin.dev.geekcity.tech:32443
    * phpmyadmin.dev.geekcity.tech should be resolved to nginx-ingress
        + for example, add `$K8S_MASTER_IP phpmyadmin.dev.geekcity.tech` to `/etc/hosts`
5. login
    * server: `mariadb.database:3306`
    * username: `root`
        + password
            * ```shell
              kubectl -n database get secret mariadb-credentials -o jsonpath='{.data.mariadb-root-password}' | base64 -d
              ```
    * username: `ben.wangz`
        + password
            * ```shell
              kubectl -n database get secret mariadb-credentials -o jsonpath='{.data.mariadb-password}' | base64 -d
              ```