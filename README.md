# God(e) code generator
A code generator with an edge

## Details: 
- [Presentation](https://sway.office.com/ykxqQy71pl4c8QzC?ref=Link)

## Requirement
- Java 11
- Maven 3.6.2
- Node 11 as default (node -v should start with v11)
- MySQL database (give credentials in gode.properties)
- Gitea (https://gitea.io/en-us/)
    - Refer https://docs.gitea.io/en-us/install-from-package/ for installation
    - Give credentials in gode.properties
- Jenkins
    - Give credentials in gode.properties
- Setup [gode-core](https://github.com/ibs-gode/gode-core) and run `mvn clean install`
- Only linux systems including mac are supported now.

## Configure Gitea
- Create user for jenkins
- Create organisation called 'ibs'
    - Specify organisation name in gode.properties
    - Create a webhook for organisation 'ibs'. Refer [the blog post](https://mike42.me/blog/2019-05-how-to-integrate-gitea-and-jenkins)
        - Customise events
- Go to `http://<Gitea IP>:<Gitea Port>/api/swagger` for verification
        
## Configure Jenkins
- Install plugins
    - Maven Integration
    - Gitea
    - Ansible
    - Terraform 
- Go to 'New Item' > Setup 'Gitea Organisation'
    - Name: Gode Artifacts
    - Give Gitea credentials
    - Project Recognizers: build/Pipeline
- Do global tool configuration 
    - maven as 'maven'
    - jdk as 'jdk11'
    - terraform as 'terraform'

## How to build
- Clone the project
- Maven build
    - `mvn clean install`
- Run the yarn build for IDE support
    - `yarn`
    - `yarn theia build`
- Give executable permissions to `ide.sh` and `terminal.sh`    
- Start the main spring boot app in `GodeGenerator` 
- Sample requests are available in sample-requests folder


## How to edit pipeline
- Provide 'pipelineGeneration' as true in BuildModel during code generation
- After successful build, call 'Edit pipeline' endpoint in swagger. It will give you a edit url.
- Edit the pipeline and checkin the change using 'Checkin endpoint'
- Refer [Jenkins documentation](https://www.jenkins.io/doc/book/pipeline/)
