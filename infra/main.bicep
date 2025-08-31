// ==============================
// main.bicep  (IaC phase)
// Deploys:
// - AKS (autoscaler + optional Log Analytics)
// - Azure MySQL Flexible Server + DBs + a lab firewall rule
// - Storage Account + PDFs container
// - Static Public IP (Standard/Static/IPv4) for ingress-nginx
// ==============================

@description('Azure region for all resources')
param location string = 'westeurope'

@description('Project seed for names')
param projectName string = 'tfg'

@description('AKS cluster name')
param aksName string = 'aks-tfg-cluster'

@description('AKS agent VM size')
param aksVmSize string = 'Standard_B2s'

@minValue(1)
@description('Initial node count (autoscaler will adjust between min/max)')
param aksNodeCount int = 1

@minValue(1)
@description('Cluster Autoscaler: minimum nodes')
param aksMinCount int = 1

@minValue(1)
@description('Cluster Autoscaler: maximum nodes')
param aksMaxCount int = 3

@description('Create a Log Analytics Workspace for AKS insights')
param createLogAnalytics bool = true

@description('Log Analytics workspace name')
param lawName string = 'log-' + projectName

@description('MySQL Flexible Server name (globally unique)')
param mysqlServerName string = 'mysql-tfg-server'

@description('MySQL admin username')
param mysqlAdminUser string = 'mysqladmin'

@secure()
@description('MySQL admin password')
param mysqlAdminPassword string

@description('MySQL version (use a supported value, e.g. 8.4)')
param mysqlVersion string = '8.4'

@description('MySQL SKU name (e.g., Standard_B1ms)')
param mysqlSkuName string = 'Standard_B1ms'

@minValue(20)
@description('MySQL storage size in GB')
param mysqlStorageGB int = 32

@description('MySQL databases to create on the flexible server')
param mysqlDatabases array = [
  'service1_db'
  'service2_db'
  'service3_db'
]

@description('Storage Account name (must be globally unique)')
param storageAccountName string = 'tfgpdfs'

@description('Blob container for PDFs')
param pdfContainerName string = 'resultspdf'

@description('Name for the Static Public IP used by ingress-nginx')
param ingressPublicIpName string = 'pip-ingress-tfg'

// ------------------------------
// Log Analytics (optional)
// ------------------------------
@description('Log Analytics Workspace for AKS monitoring')
resource law 'Microsoft.OperationalInsights/workspaces@2022-10-01' = if (createLogAnalytics) {
  name: lawName
  location: location
  properties: {
    retentionInDays: 30
    features: {
      enableLogAccessUsingOnlyResourcePermissions: true
    }
  }
}

// ------------------------------
// AKS (Managed Cluster)
// ------------------------------
resource aks 'Microsoft.ContainerService/managedClusters@2024-05-01' = {
  name: aksName
  location: location
  identity: {
    type: 'SystemAssigned'
  }
  sku: {
    name: 'Base'
    tier: 'Free'
  }
  properties: {
    // Let Azure pick a supported version
    kubernetesVersion: ''
    dnsPrefix: '${aksName}-${uniqueString(resourceGroup().id)}'
    agentPoolProfiles: [
      {
        name: 'nodepool1'
        count: aksNodeCount
        vmSize: aksVmSize
        osType: 'Linux'
        type: 'VirtualMachineScaleSets'
        mode: 'System'
        enableAutoScaling: true
        minCount: aksMinCount
        maxCount: aksMaxCount
        orchestratorVersion: ''
      }
    ]
    networkProfile: {
      networkPlugin: 'kubenet'
      loadBalancerSku: 'standard'
      outboundType: 'loadBalancer'
    }
    addonProfiles: createLogAnalytics ? {
      omsagent: {
        enabled: true
        config: {
          logAnalyticsWorkspaceResourceID: law.id
        }
      }
    } : {}
    enableRBAC: true
    apiServerAccessProfile: {
      enablePrivateCluster: false
    }
  }
}

// ------------------------------
// MySQL Flexible Server
// ------------------------------
resource mysql 'Microsoft.DBforMySQL/flexibleServers@2023-12-30' = {
  name: mysqlServerName
  location: location
  sku: {
    name: mysqlSkuName
    tier: 'Burstable' // adjust for GP/MO if needed
  }
  properties: {
    version: mysqlVersion
    administratorLogin: mysqlAdminUser
    administratorLoginPassword: mysqlAdminPassword
    backup: {
      backupRetentionDays: 7
      geoRedundantBackup: 'Disabled'
    }
    highAvailability: {
      mode: 'Disabled'
    }
    network: {
      // LAB: public access enabled; use private endpoints/VNET in prod
      publicNetworkAccess: 'Enabled'
    }
    storage: {
      storageSizeGB: mysqlStorageGB
      autoGrow: 'Enabled'
    }
  }
}

// LAB firewall rule (allow all). DO NOT USE IN PROD.
resource mysqlAllowAll 'Microsoft.DBforMySQL/flexibleServers/firewallRules@2023-12-30' = {
  name: 'allowAll'
  parent: mysql
  properties: {
    startIpAddress: '0.0.0.0'
    endIpAddress: '255.255.255.255'
  }
}

// DBs
@batchSize(1)
resource mysqlDbs 'Microsoft.DBforMySQL/flexibleServers/databases@2023-12-30' = [for dbName in mysqlDatabases: {
  name: dbName
  parent: mysql
}]

// ------------------------------
// Storage Account + Container
// ------------------------------
resource storage 'Microsoft.Storage/storageAccounts@2023-01-01' = {
  name: storageAccountName
  location: location
  sku: {
    name: 'Standard_LRS'
  }
  kind: 'StorageV2'
  properties: {
    allowBlobPublicAccess: false
    minimumTlsVersion: 'TLS1_2'
    supportsHttpsTrafficOnly: true
  }
}

resource blobService 'Microsoft.Storage/storageAccounts/blobServices@2023-01-01' = {
  name: '${storage.name}/default'
  properties: {}
  dependsOn: [ storage ]
}

resource pdfContainer 'Microsoft.Storage/storageAccounts/blobServices/containers@2023-01-01' = {
  name: '${storage.name}/default/${pdfContainerName}'
  properties: {
    publicAccess: 'None'
  }
  dependsOn: [ blobService ]
}

// ------------------------------
// Static Public IP for ingress-nginx
// (We create it in the *main* RG to keep names predictable.
// Your script will point the Service annotation
// service.beta.kubernetes.io/azure-load-balancer-resource-group = this RG.)
// ------------------------------
resource ingressPip 'Microsoft.Network/publicIPAddresses@2023-04-01' = {
  name: ingressPublicIpName
  location: location
  sku: {
    name: 'Standard'
  }
  properties: {
    publicIPAllocationMethod: 'Static'
    publicIPAddressVersion: 'IPv4'
  }
}

// ------------------------------
// Outputs
// ------------------------------
@description('AKS node resource group (created/managed by AKS)')
output aksNodeResourceGroup string = aks.properties.nodeResourceGroup

@description('MySQL Flexible Server FQDN (host)')
output mysqlFqdn string = mysql.properties.fullyQualifiedDomainName

@description('Azure Storage connection string for the account')
output storageConnectionString string = 'DefaultEndpointsProtocol=https;AccountName=${storage.name};AccountKey=${listKeys(storage.id, ''2021-08-01'').keys[0].value};EndpointSuffix=${environment().suffixes.storage}'

@description('Static Public IP resource ID')
output ingressPublicIpId string = ingressPip.id

@description('Static Public IP address (use in nip.io host)')
output ingressPublicIp string = ingressPip.properties.ipAddress

@description('Suggested frontend host (nip.io)')
output frontendHost string = 'frontend.' + ingressPip.properties.ipAddress + '.nip.io'
