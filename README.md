# God(e) code generator
A code generator with an edge

## Details: 
- https://sway.office.com/ykxqQy71pl4c8QzC?ref=Link

## Requirement
- Java 11
- Node 11 as default (node -v should start with v11)
- MySQL database (give credentials in gode.properties)
- Gitea (https://gitea.io/en-us/)
    - Refer https://docs.gitea.io/en-us/install-from-package/ for installation
    - Give credentials in gode.properties
- Jenkins
    - Give credentials in gode.properties

## Configure Gitea
- Create user for jenkins
- Create organisation called 'ibs'
    - Specify organisation name in gode.properties
    - Create a webhook for organisation 'ibs'. Refer https://mike42.me/blog/2019-05-how-to-integrate-gitea-and-jenkins
        - Customise events
- Go to http://<Gitea IP>:<Gitea Port>/api/swagger for verification
        
## Configure Jenkins
- Install plugins
    - Maven Integration
    - Gitea 
- Go to 'New Item' > Setup 'Gitea Organisation'
    - Name: Gode Artifacts
    - Give Gitea credentials
    - Project Recognizers: build/Pipeline

## How to build
- Clone the project
- Maven build
    - `mvn clean install`
- Run the yarn build for IDE support
    - `yarn`
    - `yarn theia build`
- Start the main spring boot app in `GodeGenerator` 
- Sample requests are available in sample-requests folder