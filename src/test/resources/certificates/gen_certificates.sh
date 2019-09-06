# Root CA
openssl genrsa -out rootCA.key
openssl req -x509 -new -nodes -key rootCA.key -sha256 -days 3654 -subj "/C=FR/O=Fluent Http/CN=Root CA" -out rootCA.crt

# Sub CA
openssl genrsa -out subCA.key
openssl req -sha256 -new -key subCA.key -subj "/C=FR/O=Fluent Http/CN=Sub CA" -out subCA.csr
openssl x509 -req -extfile v3.ext -in subCA.csr -CA rootCA.crt -CAkey rootCA.key -CAcreateserial -days 3654 -sha256 -out subCA.crt

# Host certificate
openssl genrsa -out localhost.key
openssl req -sha256 -new -key localhost.key -subj "/C=FR/O=Fluent Http/CN=localhost" -out localhost.csr
openssl x509 -req -extfile v3.ext -in localhost.csr -CA subCA.crt -CAkey subCA.key -CAcreateserial -out localhost.crt -days 3654 -sha256

# Der private key:
openssl pkcs8 -topk8 -inform PEM -outform DER -in localhost.key -out localhost.der -nocrypt

# Http Client PFX certificate 
openssl pkcs12 -export -inkey localhost.key -in localhost.crt -password pass:password -out localhost.pfx

