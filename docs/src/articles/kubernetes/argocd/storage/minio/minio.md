# minio

## prepare

1. k8s is ready
2. argocd is ready and logged in

## installation

1. prepare secret for root user credentials
    * ```shell
      kubectl get namespaces storage > /dev/null 2>&1 || kubectl create namespace storage
      kubectl -n storage create secret generic minio-secret \
          --from-literal=rootUser=admin \
          --from-literal=rootPassword=$(tr -dc A-Za-z0-9 </dev/urandom | head -c 16)
      ```
2. prepare `minio.yaml`
    * ```yaml
      <!-- @include: minio.yaml -->
      ```
3. apply to k8s
    * ```shell
      kubectl -n argocd apply -f minio.yaml
      ```
4. sync by argocd
    * ```shell
      argocd app sync argocd/minio
5. visit minio console
    * minio-console.dev.geekcity.tech should be resolved to nginx-ingress
        + for example, add `$K8S_MASTER_IP minio-console.dev.geekcity.tech` to `/etc/hosts`
    * address: http://minio-console.dev.geekcity.tech:32080/login
    * access key: admin
    * access secret
        + ```shell
          kubectl -n storage get secret minio-secret -o jsonpath='{.data.rootPassword}' | base64 -d
          ```

## references
* https://github.com/minio/minio/tree/master/helm/minio