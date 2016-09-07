<s:Envelope xmlns:s="http://www.w3.org/2003/05/soap-envelope">
  <s:Body>
    <GetResult xmlns="http://www.relatics.com/">
      <Operation>${reportName}</Operation>
      <Identification>
        <Relatics:Identification xmlns:Relatics="http://www.relatics.com/">
          <Relatics:Workspace>${workspace}</Relatics:Workspace>
        </Relatics:Identification>
      </Identification>
        <Parameters>
            <Relatics:Parameters xmlns:Relatics="http://www.relatics.com/">
              
              <#list parameters?keys as name>
                <Relatics:Parameter Name="${name}" Value="${parameters[name]}"/>
              </#list>
            
            </Relatics:Parameters>
        </Parameters>
      <Authentication>
        <Relatics:Authentication xmlns:Relatics="http://www.relatics.com/">
          <Relatics:Entrycode>${entryCode}</Relatics:Entrycode>
        </Relatics:Authentication>
      </Authentication>
    </GetResult>
  </s:Body>
</s:Envelope>