## How certificates have been generated

### Root CA
```
openssl genrsa -out rootCA.key
openssl req -x509 -new -nodes -key rootCA.key -sha256 -days 3654 -out rootCA.crt
```
### Sub CA
```
openssl genrsa -out subCA.key
openssl req -sha256 -new -key subCA.key -out subCA.csr
openssl x509 -req -extfile v3.ext -in subCA.csr -CA rootCA.crt -CAkey rootCA.key -CAcreateserial -out subCA.crt -days 3654 -sha256
```
### Host certificate
```
openssl genrsa -out localhost.key
openssl req -sha256 -new -key localhost.key -out localhost.csr
openssl x509 -req -extfile v3.ext -in localhost.csr -CA subCA.crt -CAkey subCA.key -CAcreateserial -out localhost.crt -days 3654 -sha256
```
Der private key:
```
openssl pkcs8 -topk8 -inform PEM -outform DER -in localhost.key -out localhost.der -nocrypt
```
### Client certificate
PFX file to authenticate : 
```
openssl pkcs12 -export -inkey localhost.key -in localhost.crt -out localhost.pfx
```
Then type "password" when openssl asks for export password.
