package cz.woitee.endlessRunners.evolution.evoController.evolved

import cz.woitee.endlessRunners.evolution.evoController.EvolvedPlayerController
import cz.woitee.endlessRunners.utils.JavaSerializationUtils
import io.jenetics.DoubleGene
import io.jenetics.Genotype

/**
 * The best controller (neural-network) evolved for the crouch game.
 */
class BestEvolvedCrouchController : EvolvedPlayerController(JavaSerializationUtils.unserializeFromString<Genotype<DoubleGene>>("-84:-19:0:5:115:114:0:20:105:111:46:106:101:110:101:116:105:99:115:46:71:101:110:111:116:121:112:101:0:0:0:0:0:0:0:3:2:0:2:76:0:12:95:99:104:114:111:109:111:115:111:109:101:115:116:0:23:76:105:111:47:106:101:110:101:116:105:99:115:47:117:116:105:108:47:73:83:101:113:59:76:0:6:95:118:97:108:105:100:116:0:19:76:106:97:118:97:47:108:97:110:103:47:66:111:111:108:101:97:110:59:120:112:115:114:0:41:105:111:46:106:101:110:101:116:105:99:115:46:105:110:116:101:114:110:97:108:46:99:111:108:108:101:99:116:105:111:110:46:65:114:114:97:121:73:83:101:113:0:0:0:0:0:0:0:1:2:0:0:120:114:0:40:105:111:46:106:101:110:101:116:105:99:115:46:105:110:116:101:114:110:97:108:46:99:111:108:108:101:99:116:105:111:110:46:65:114:114:97:121:83:101:113:0:0:0:0:0:0:0:1:2:0:1:76:0:5:97:114:114:97:121:116:0:39:76:105:111:47:106:101:110:101:116:105:99:115:47:105:110:116:101:114:110:97:108:47:99:111:108:108:101:99:116:105:111:110:47:65:114:114:97:121:59:120:112:115:114:0:37:105:111:46:106:101:110:101:116:105:99:115:46:105:110:116:101:114:110:97:108:46:99:111:108:108:101:99:116:105:111:110:46:65:114:114:97:121:0:0:0:0:0:0:0:1:3:0:2:73:0:7:95:108:101:110:103:116:104:73:0:6:95:115:116:97:114:116:120:112:0:0:0:17:0:0:0:0:119:1:1:115:114:0:43:105:111:46:106:101:110:101:116:105:99:115:46:105:110:116:101:114:110:97:108:46:99:111:108:108:101:99:116:105:111:110:46:79:98:106:101:99:116:83:116:111:114:101:0:0:0:0:0:0:0:1:2:0:1:91:0:6:95:97:114:114:97:121:116:0:19:91:76:106:97:118:97:47:108:97:110:103:47:79:98:106:101:99:116:59:120:112:117:114:0:19:91:76:106:97:118:97:46:108:97:110:103:46:79:98:106:101:99:116:59:-112:-50:88:-97:16:115:41:108:2:0:0:120:112:0:0:0:17:115:114:0:28:105:111:46:106:101:110:101:116:105:99:115:46:68:111:117:98:108:101:67:104:114:111:109:111:115:111:109:101:0:0:0:0:0:0:0:2:3:0:0:120:114:0:37:105:111:46:106:101:110:101:116:105:99:115:46:65:98:115:116:114:97:99:116:66:111:117:110:100:101:100:67:104:114:111:109:111:115:111:109:101:0:0:0:0:0:0:0:1:2:0:2:76:0:4:95:109:97:120:116:0:22:76:106:97:118:97:47:108:97:110:103:47:67:111:109:112:97:114:97:98:108:101:59:76:0:4:95:109:105:110:113:0:126:0:17:120:114:0:30:105:111:46:106:101:110:101:116:105:99:115:46:86:97:114:105:97:98:108:101:67:104:114:111:109:111:115:111:109:101:0:0:0:0:0:0:0:1:2:0:1:76:0:12:95:108:101:110:103:116:104:82:97:110:103:101:116:0:27:76:105:111:47:106:101:110:101:116:105:99:115:47:117:116:105:108:47:73:110:116:82:97:110:103:101:59:120:114:0:30:105:111:46:106:101:110:101:116:105:99:115:46:65:98:115:116:114:97:99:116:67:104:114:111:109:111:115:111:109:101:0:0:0:0:0:0:0:1:2:0:0:120:112:115:114:0:25:105:111:46:106:101:110:101:116:105:99:115:46:117:116:105:108:46:73:110:116:82:97:110:103:101:0:0:0:0:0:0:0:1:2:0:2:73:0:4:95:109:97:120:73:0:4:95:109:105:110:120:112:0:0:0:50:0:0:0:49:115:114:0:16:106:97:118:97:46:108:97:110:103:46:68:111:117:98:108:101:-128:-77:-62:74:41:107:-5:4:2:0:1:68:0:5:118:97:108:117:101:120:114:0:16:106:97:118:97:46:108:97:110:103:46:78:117:109:98:101:114:-122:-84:-107:29:11:-108:-32:-117:2:0:0:120:112:63:-16:0:0:0:0:0:0:115:113:0:126:0:24:-65:-16:0:0:0:0:0:0:119:4:0:0:0:49:113:0:126:0:23:122:0:0:1:-104:-65:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:-65:-105:56:33:-47:7:89:32:-65:-35:-79:-60:118:-59:-6:-39:-65:-29:22:-75:-118:35:-45:122:-65:-105:-91:-98:88:97:80:64:63:-41:-39:-56:2:-88:49:96:63:-16:0:0:0:0:0:0:63:-99:89:-99:-39:42:28:0:-65:-23:-94:-11:-58:-70:-65:-30:-65:-63:-73:87:-72:-78:-9:20:63:-31:9:89:-58:62:-27:75:63:-39:-115:-37:120:-50:35:22:63:-64:-114:81:47:-23:124:-58:63:-38:-17:-24:62:-50:-9:-47:-65:-29:107:-104:66:54:95:1:-65:-16:0:0:0:0:0:0:-65:-82:-97:-9:-95:-38:-15:40:63:-20:71:7:100:-72:18:-71:63:-26:97:11:74:-74:28:92:63:-30:-19:23:41:49:3:-4:63:-60:-16:81:111:92:51:122:-65:-28:13:57:-83:-90:89:24:-65:-42:40:114:-100:38:-76:33:-65:-33:118:-99:44:-125:-85:-35:63:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:-65:-23:-12:-112:-20:-90:73:126:-65:-51:41:82:-62:72:-40:-115:-65:-51:-44:-35:-114:101:-11:-64:63:-52:-102:-77:78:-99:123:-116:63:-16:0:0:0:0:0:0:-65:-21:104:-47:4:64:-76:33:-65:-26:-39:-42:22:-64:-15:106:-65:-84:25:125:14:-113:-49:-113:-65:-46:-57:97:58:-107:-12:57:63:-47:75:26:37:-53:-31:-81:63:-62:81:70:-105:-102:28:25:-65:-42:20:-87:87:27:-85:4:63:-42:-125:-55:-56:-33:-36:-60:-65:42:-55:123:-54:-120:-112:0:63:-37:-90:30:-12:25:-123:25:63:-30:-63:-19:72:-107:-2:-128:-65:-16:0:0:0:0:0:0:-65:-28:99:65:-112:15:70:62:63:-22:89:114:112:-69:63:25:-65:-57:-14:9:-82:34:-66:34:63:-37:94:-8:-126:19:41:-40:-65:-26:46:-71:-106:58:-75:-22:63:-70:-59:107:-116:79:-124:-30:120:115:113:0:126:0:15:115:113:0:126:0:22:0:0:0:50:0:0:0:49:115:113:0:126:0:24:63:-16:0:0:0:0:0:0:115:113:0:126:0:24:-65:-16:0:0:0:0:0:0:119:4:0:0:0:49:113:0:126:0:29:122:0:0:1:-104:-65:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:63:-91:-76:-90:-41:-90:7:48:-65:-29:-9:27:67:0:-56:52:-65:-69:-119:-87:-49:-54:-35:0:63:-30:-103:25:47:-22:21:-10:63:-81:121:-109:50:-10:79:96:63:-91:-67:-53:29:80:-53:12:-65:-37:100:-128:-124:112:-61:-99:-65:-24:120:25:125:27:109:-28:63:-16:0:0:0:0:0:0:63:-54:72:116:24:-78:-32:88:63:-48:13:-5:86:126:-67:28:-65:-22:-106:75:-86:16:88:-114:-65:-16:0:0:0:0:0:0:-65:-32:77:126:-115:102:24:-72:-65:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:-65:-93:0:-22:60:-19:3:0:-65:-29:-13:-1:35:-8:-70:54:-65:-60:119:-88:-52:39:57:52:-65:-25:-126:91:32:88:-55:-106:63:-25:27:-97:-65:34:5:74:63:-54:-22:18:119:26:-116:-104:63:-37:90:-39:109:37:-115:-57:63:-96:-82:-106:25:-23:-122:-60:63:-20:4:22:-106:106:39:111:-65:-39:11:123:123:110:47:123:63:-72:92:51:-33:-58:-9:24:-65:-16:0:0:0:0:0:0:-65:-16:0:0:0:0:0:0:-65:-16:0:0:0:0:0:0:-65:-16:0:0:0:0:0:0:-65:-16:0:0:0:0:0:0:-65:-28:19:99:24:-68:44:-56:63:-67:2:97:12:11:53:72:63:-36:-2:51:-98:97:-72:-85:-65:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:-65:-30:11:77:28:54:-109:38:63:-16:0:0:0:0:0:0:63:-30:-45:1:-78:-29:85:34:-65:-16:0:0:0:0:0:0:63:-30:-106:9:112:-37:-52:26:-65:-16:0:0:0:0:0:0:63:-30:2:24:-115:101:-91:-120:-65:-43:-24:-41:20:123:87:64:63:-33:53:-97:-66:-67:21:6:-65:-46:33:-17:-57:80:-63:92:63:-42:95:-93:-99:-127:-46:-43:63:-27:-120:-5:-52:-35:-31:-92:120:115:113:0:126:0:15:115:113:0:126:0:22:0:0:0:50:0:0:0:49:115:113:0:126:0:24:63:-16:0:0:0:0:0:0:115:113:0:126:0:24:-65:-16:0:0:0:0:0:0:119:4:0:0:0:49:113:0:126:0:33:122:0:0:1:-104:-65:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:63:-41:77:-36:-85:-106:110:117:-65:-28:-51:-75:-124:58:-3:66:-65:-16:0:0:0:0:0:0:-65:-34:-26:-40:-79:111:-72:-67:-65:-22:97:97:-88:-45:-2:15:63:-16:0:0:0:0:0:0:-65:-93:71:-91:46:-64:-55:-122:-65:-84:43:-26:12:-108:24:112:-65:-23:-40:-41:115:68:-25:-107:-65:-32:-106:54:32:50:-108:57:-65:-16:0:0:0:0:0:0:-65:-38:-19:56:-26:-119:-38:126:-65:-20:72:-115:-108:-48:-75:-4:63:-31:5:-127:-86:19:83:55:63:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:-65:-22:69:52:-107:-121:-21:13:63:-23:-2:66:12:-30:56:-14:63:-16:0:0:0:0:0:0:-65:-16:0:0:0:0:0:0:-65:-29:46:57:-64:6:-40:125:-65:-28:13:65:-96:15:25:75:-65:-59:-10:27:9:0:56:88:63:-24:0:11:-79:97:-10:110:-65:-96:113:55:62:67:-27:47:-65:-38:40:-36:-51:-43:-116:64:-65:-22:-73:46:114:-64:-115:77:-65:-16:0:0:0:0:0:0:63:-37:90:-32:39:17:28:4:-65:-48:114:-26:30:76:-29:-80:63:-55:-1:6:78:109:78:60:-65:-32:78:-117:-114:-105:82:-41:-65:-20:22:46:0:40:33:-125:-65:-28:93:-91:-65:-58:-23:79:-65:-37:-50:-127:20:89:-60:-12:63:-16:0:0:0:0:0:0:-65:-79:-51:107:38:-10:-108:63:-65:-21:-87:15:37:-3:100:123:63:-39:43:-3:-103:36:-8:64:-65:-30:48:119:-122:87:105:-11:-65:-16:0:0:0:0:0:0:63:-28:10:-104:70:73:111:-40:-65:104:-11:-9:-50:-58:-38:-128:-65:-32:-49:-73:38:-21:-66:49:-65:-17:-78:56:-13:106:60:60:-65:-26:10:-89:-27:59:-87:-18:-65:-45:-60:41:-67:63:-51:-16:-65:-42:-95:4:-124:-60:112:-91:120:115:113:0:126:0:15:115:113:0:126:0:22:0:0:0:50:0:0:0:49:115:113:0:126:0:24:63:-16:0:0:0:0:0:0:115:113:0:126:0:24:-65:-16:0:0:0:0:0:0:119:4:0:0:0:49:113:0:126:0:37:122:0:0:1:-104:-65:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:-65:-55:-123:-56:91:115:-48:-66:63:-67:-97:96:-40:101:28:24:63:-67:25:-94:-28:68:-107:-18:-65:-16:0:0:0:0:0:0:-65:-21:-8:-18:-52:121:96:-110:-65:-18:-20:-5:60:16:-24:-121:-65:-46:95:-90:77:65:-105:27:63:-39:22:-67:87:-121:-22:52:63:-16:0:0:0:0:0:0:-65:-67:-97:35:-39:31:37:62:63:-28:-107:-115:16:74:-11:-88:-65:-27:10:15:-59:17:-28:60:-65:-34:17:57:15:-33:70:0:-65:-40:39:14:-25:44:65:17:-65:-24:-49:38:57:47:-35:72:63:-64:-2:1:1:-26:37:-28:-65:-20:37:122:-36:69:22:27:-65:-17:76:-128:26:60:-115:-35:-65:-31:99:15:55:72:102:-100:63:-23:-127:107:-115:-76:54:-122:-65:-24:1:-120:-118:-35:80:54:-65:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:-65:-18:30:-48:41:75:6:103:-65:-115:-91:48:101:-68:71:-48:63:-49:11:-4:73:23:7:124:63:-26:-34:10:-71:-13:-110:-22:-65:-16:0:0:0:0:0:0:63:-32:69:98:118:121:124:-16:-65:-16:0:0:0:0:0:0:63:-110:-38:10:102:-18:97:-8:63:-44:61:-33:-117:100:-117:28:63:-96:-28:33:-54:-122:113:68:-65:-22:-121:-123:-123:-99:-38:-35:63:-34:23:89:100:32:114:-103:-65:-23:-120:88:122:47:93:116:-65:-35:-52:-76:-95:10:48:-7:-65:-16:0:0:0:0:0:0:63:-50:60:85:-16:38:89:-100:63:-31:11:-44:54:105:41:0:63:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:-65:-22:12:112:-87:115:3:-38:-65:-16:0:0:0:0:0:0:63:-55:65:92:-19:-70:-24:-29:63:-23:-93:-59:127:98:63:-96:63:-16:0:0:0:0:0:0:63:-27:98:33:105:2:-53:50:63:-40:99:-60:15:-53:-125:-32:120:115:113:0:126:0:15:115:113:0:126:0:22:0:0:0:50:0:0:0:49:115:113:0:126:0:24:63:-16:0:0:0:0:0:0:115:113:0:126:0:24:-65:-16:0:0:0:0:0:0:119:4:0:0:0:49:113:0:126:0:41:122:0:0:1:-104:-65:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:-65:-49:31:108:93:-73:101:104:63:-61:-36:47:-108:-24:53:-128:-65:-94:9:-101:116:122:-33:96:63:-31:93:93:-74:-61:-18:-44:63:-16:0:0:0:0:0:0:63:-23:11:50:-44:41:88:83:63:-44:64:-118:59:12:63:-74:63:-16:0:0:0:0:0:0:-65:-43:-41:-69:1:-44:-2:-104:63:-27:-118:9:2:79:91:60:-65:-59:40:-25:-95:109:-107:112:63:-16:0:0:0:0:0:0:-65:-32:-124:-123:98:53:-41:-74:63:-28:29:27:9:-8:3:-71:-65:-43:15:-93:-52:-114:-64:46:-65:-58:-94:18:91:-7:76:-88:-65:-30:-84:-44:-93:-16:6:-64:-65:-33:-17:-31:67:-39:-97:-88:63:-27:-72:80:119:-45:-16:111:63:-41:-59:75:43:104:42:-104:-65:-25:-62:-11:45:-96:-105:58:63:-44:-12:-112:-71:4:120:-90:-65:-67:-84:83:5:-76:-114:-104:-65:-45:16:88:-19:58:86:-25:63:-65:94:84:-102:-104:64:97:-65:-24:-55:-35:86:-45:68:-71:-65:-39:12:-76:-95:-12:73:-52:-65:-80:-52:5:55:-54:110:89:63:-16:0:0:0:0:0:0:-65:-63:92:64:121:76:-85:-120:63:-16:0:0:0:0:0:0:-65:-18:-111:-8:65:11:-21:-49:-65:-45:99:-22:-45:108:-122:-66:-65:-28:-83:-46:-51:18:-115:-63:-65:-16:0:0:0:0:0:0:63:-56:105:18:55:-59:-56:-120:63:-16:0:0:0:0:0:0:63:-46:-52:110:-7:-124:-2:120:63:-16:0:0:0:0:0:0:-65:-24:55:-69:-99:125:-29:-92:63:-24:-123:25:-29:-69:-118:-35:63:-29:-28:87:-37:-110:-116:33:-65:-17:89:-47:-109:-79:-23:89:-65:-47:-25:-27:-115:33:-62:-94:63:-38:-44:-2:-23:0:-31:107:-65:-58:117:-48:-110:11:-62:-107:63:-26:-85:-102:26:75:-122:-60:63:-61:-76:47:83:-120:-61:89:-65:-16:0:0:0:0:0:0:120:115:113:0:126:0:15:115:113:0:126:0:22:0:0:0:50:0:0:0:49:115:113:0:126:0:24:63:-16:0:0:0:0:0:0:115:113:0:126:0:24:-65:-16:0:0:0:0:0:0:119:4:0:0:0:49:113:0:126:0:45:122:0:0:1:-104:-65:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:-65:-46:99:21:55:-108:122:-125:-65:-45:32:-91:-5:-46:-63:-10:63:-16:0:0:0:0:0:0:63:-18:-100:21:-32:106:15:-85:-65:-54:-88:-60:-71:79:-70:-84:-65:-80:127:-56:-37:52:110:92:63:-16:0:0:0:0:0:0:-65:-26:84:68:20:11:-20:32:-65:-16:0:0:0:0:0:0:63:-28:86:69:40:89:-45:122:-65:-16:0:0:0:0:0:0:-65:-16:0:0:0:0:0:0:-65:-53:76:-102:65:126:17:-122:63:-65:97:-106:69:75:-89:80:63:-39:-108:-69:45:52:78:60:63:-40:27:21:31:7:57:125:-65:-16:0:0:0:0:0:0:-65:-77:-80:105:38:76:125:96:-65:-30:-42:72:-1:-2:114:43:63:-57:115:-82:121:42:-111:84:63:-31:-49:-11:-123:-31:110:83:-65:-32:-89:59:96:-117:98:6:-65:-46:103:-14:35:64:-43:-96:63:-98:44:-88:55:114:65:4:63:-16:0:0:0:0:0:0:-65:-22:-7:-19:115:119:-77:-27:63:-27:53:-75:-17:123:-25:44:-65:-41:-18:59:-50:11:-92:96:63:-29:84:-61:-35:7:-21:58:63:-18:-38:117:92:-80:-12:16:-65:-87:-17:39:15:82:-47:0:63:-16:0:0:0:0:0:0:-65:-25:-56:41:-91:54:-119:110:-65:-36:123:124:55:3:-91:-65:-65:-16:0:0:0:0:0:0:-65:-58:-2:-39:-16:34:48:94:-65:-55:-24:125:95:-93:-31:4:63:-39:61:-14:-58:125:25:-20:63:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:63:-17:-37:-15:-87:16:34:-120:63:-54:-2:-5:-52:-25:-21:8:-65:-16:0:0:0:0:0:0:63:-59:47:-113:-120:90:-42:-28:63:-27:-72:-58:54:-68:-119:122:63:-43:-5:-41:-19:-30:-124:96:63:-41:64:-12:12:-39:-94:-32:-65:-54:-126:-20:58:-73:118:0:120:115:113:0:126:0:15:115:113:0:126:0:22:0:0:0:50:0:0:0:49:115:113:0:126:0:24:63:-16:0:0:0:0:0:0:115:113:0:126:0:24:-65:-16:0:0:0:0:0:0:119:4:0:0:0:49:113:0:126:0:49:122:0:0:1:-104:-65:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:-65:-93:-10:-86:88:50:-72:96:63:-30:-34:70:-6:84:65:63:-65:-16:0:0:0:0:0:0:63:-45:98:84:-117:82:96:102:-65:-16:0:0:0:0:0:0:63:-56:9:104:-71:111:-11:-2:-65:-16:0:0:0:0:0:0:-65:-19:-50:116:28:-89:-78:-60:-65:-16:0:0:0:0:0:0:63:-43:80:-110:-117:67:98:19:-65:-25:120:124:-2:14:24:-124:-65:-25:78:-99:-108:-106:-91:14:63:-61:105:-29:-35:7:95:70:63:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:63:-27:-18:-2:-35:12:-120:58:-65:-33:-84:-74:-17:124:77:41:-65:-72:-63:-117:44:-102:105:-110:63:-43:97:-20:-84:-9:-53:1:63:-18:12:48:-16:98:69:-74:-65:-23:-77:10:-5:29:72:-44:-65:-27:-47:113:34:4:82:-114:63:-36:-36:-79:87:40:81:-56:-65:-29:16:-38:-11:72:-5:114:-65:-16:0:0:0:0:0:0:63:-53:-118:-47:-87:18:34:-106:-65:-39:22:75:76:39:40:82:63:-34:123:-39:-118:27:-51:-20:63:-47:-44:112:-79:111:-71:-32:-65:-16:0:0:0:0:0:0:-65:-24:-6:103:-18:-75:-40:-26:-65:-37:-70:-95:31:-95:-33:94:-65:-31:20:-108:-108:-74:-65:124:-65:-16:0:0:0:0:0:0:-65:-43:-4:66:88:63:14:-108:63:-18:111:-27:70:120:-32:42:63:-16:0:0:0:0:0:0:63:-25:-63:19:-55:26:85:-74:63:-16:0:0:0:0:0:0:-65:-116:-55:-84:17:126:-2:64:-65:-25:-8:-19:85:-96:84:-85:63:-79:-66:-15:-21:-61:-124:16:63:-26:21:-123:-52:-12:68:118:-65:-33:46:-54:-19:-49:-99:-62:63:-50:-15:-107:48:23:31:-1:63:-46:-23:30:-20:24:-110:-121:-65:-40:-74:79:-116:36:-29:73:63:-77:-46:7:67:-23:-59:-72:-65:-17:49:20:-83:-106:-117:44:120:115:113:0:126:0:15:115:113:0:126:0:22:0:0:0:50:0:0:0:49:115:113:0:126:0:24:63:-16:0:0:0:0:0:0:115:113:0:126:0:24:-65:-16:0:0:0:0:0:0:119:4:0:0:0:49:113:0:126:0:53:122:0:0:1:-104:-65:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:-65:-28:-65:-39:-81:110:-34:-35:63:-60:-1:106:42:126:119:-68:-65:-22:-34:-75:61:15:35:68:-65:-60:8:-58:-122:40:44:36:63:-22:-51:-64:80:122:96:46:63:-56:109:-5:65:19:105:88:-65:-48:-104:50:123:-112:-7:78:63:-20:102:34:76:-10:-27:98:63:-27:116:9:-113:47:-1:15:-65:-22:-121:-34:38:123:127:-103:-65:-27:2:13:-115:-20:-12:-86:63:-16:0:0:0:0:0:0:-65:-16:0:0:0:0:0:0:-65:-56:115:41:64:85:-86:40:-65:-58:73:-27:42:-18:21:96:63:-29:-40:99:-60:72:84:-13:63:-79:-5:-74:-34:25:-28:48:63:-75:-55:107:-16:82:-108:24:-65:-29:86:13:-32:-11:-35:-86:-65:-43:15:124:-43:-78:-86:124:-65:-67:-19:-109:-75:-58:8:88:63:-78:-126:-101:119:61:121:-108:-65:-26:57:112:100:78:-29:-122:63:-16:0:0:0:0:0:0:-65:-72:-109:-102:-42:-103:91:86:63:-38:-2:63:24:23:-62:112:63:-39:77:-127:-115:-108:-17:-40:63:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:-65:-34:61:96:-109:107:-125:-12:-65:-20:81:-25:68:101:96:-104:63:-16:0:0:0:0:0:0:63:-21:-118:30:60:81:124:54:63:-16:0:0:0:0:0:0:-65:-31:35:123:2:-99:3:69:63:-16:0:0:0:0:0:0:63:-19:41:-70:-85:22:-23:82:63:-16:0:0:0:0:0:0:63:-25:-69:-124:78:-10:111:-30:63:-37:-43:-84:82:-16:-46:8:63:-39:33:-38:-126:-95:64:-44:63:-19:-14:96:114:126:-31:-16:63:-32:65:-115:27:-44:-6:-19:-65:-31:-61:-68:-13:53:-78:-128:63:-16:0:0:0:0:0:0:63:-18:60:-60:-10:-127:108:102:-65:-16:0:0:0:0:0:0:-65:-30:109:75:53:-26:127:38:-65:-40:-117:-8:79:80:-125:-98:120:115:113:0:126:0:15:115:113:0:126:0:22:0:0:0:50:0:0:0:49:115:113:0:126:0:24:63:-16:0:0:0:0:0:0:115:113:0:126:0:24:-65:-16:0:0:0:0:0:0:119:4:0:0:0:49:113:0:126:0:57:122:0:0:1:-104:-65:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:63:-46:43:-40:-85:60:-117:-109:-65:-23:-33:-24:68:-87:-17:110:63:-60:74:-106:-11:19:-60:-72:-65:-31:77:-120:109:-22:78:121:-65:-16:0:0:0:0:0:0:-65:-16:0:0:0:0:0:0:-65:-68:-77:30:-4:-11:-98:-8:-65:-16:0:0:0:0:0:0:63:-35:66:91:19:6:44:-74:-65:-84:79:39:-24:-34:1:112:63:-16:0:0:0:0:0:0:63:-101:-22:66:91:-1:68:-96:63:-28:-37:-11:-80:64:48:-40:-65:-47:-38:-59:-27:-15:-73:105:63:-74:-95:-65:75:-74:-17:8:63:-16:0:0:0:0:0:0:63:-42:17:-18:-123:-82:-87:-45:63:-82:-58:60:86:107:58:-68:-65:-63:-22:-89:-11:-83:-24:-26:-65:-23:-47:-5:-75:-10:88:110:-65:-22:-56:53:-31:114:-27:90:63:-59:-110:-78:17:-88:-54:42:-65:-50:3:39:-47:16:41:105:63:-43:2:103:-11:57:119:-85:-65:-59:-7:71:55:107:-60:48:-65:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:63:-19:-47:-30:-49:84:-58:58:63:-44:-63:-79:-31:108:-51:88:63:-25:109:-35:-18:70:-43:-120:63:-16:0:0:0:0:0:0:63:-47:-55:-63:-70:35:12:106:-65:-20:87:99:49:88:-11:-79:63:-82:-55:-63:-39:-102:-79:-120:-65:-40:-6:-16:-92:-97:24:-123:63:-16:0:0:0:0:0:0:63:-22:-80:-72:-88:-102:39:11:-65:-27:-120:-49:119:-49:-45:-72:63:-51:-117:-71:5:-120:-87:100:63:-42:-117:-90:105:-96:110:-56:-65:-31:-120:-58:-81:15:119:-110:63:-24:-63:104:91:-1:88:-93:63:-31:117:7:-5:14:-66:-34:63:-32:1:15:12:67:-118:-91:63:-40:32:-95:-53:74:1:-77:-65:-16:0:0:0:0:0:0:-65:-16:0:0:0:0:0:0:63:-45:-11:-123:27:29:-62:125:63:-16:0:0:0:0:0:0:120:115:113:0:126:0:15:115:113:0:126:0:22:0:0:0:50:0:0:0:49:115:113:0:126:0:24:63:-16:0:0:0:0:0:0:115:113:0:126:0:24:-65:-16:0:0:0:0:0:0:119:4:0:0:0:49:113:0:126:0:61:122:0:0:1:-104:-65:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:-65:-123:30:-88:-5:48:97:32:63:-16:0:0:0:0:0:0:-65:-18:-60:100:58:59:70:-29:63:-16:0:0:0:0:0:0:-65:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:-65:-16:0:0:0:0:0:0:-65:-40:-17:45:58:102:92:-122:63:-33:14:-11:-118:112:-100:-101:63:-20:72:33:-89:4:51:-42:63:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:-65:-55:119:-63:115:-35:97:108:63:-30:8:-15:-46:-71:65:102:-65:113:13:-12:26:-8:-113:-128:63:-27:98:-39:124:68:-120:-17:-65:-61:-57:-70:38:102:58:34:63:-47:-47:118:68:73:-72:-26:-65:-24:44:19:102:-98:-75:-109:63:-16:0:0:0:0:0:0:-65:-62:33:-29:39:116:-123:92:-65:-32:-103:65:-36:36:-38:70:-65:-51:76:-53:52:86:-53:16:63:-40:-110:106:-106:-67:58:-123:-65:-27:-103:-68:-14:103:-33:-70:-65:-41:-44:-41:-16:-18:77:-90:63:-16:0:0:0:0:0:0:63:-46:-65:1:106:-1:4:-117:-65:-88:3:12:9:-41:122:-24:63:-16:0:0:0:0:0:0:-65:-78:-32:-118:116:-94:-42:-64:63:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:63:-25:-50:-73:17:41:-107:-68:63:-21:6:-28:-99:112:0:31:-65:-49:-104:85:78:-59:31:-96:-65:-74:-98:103:123:-75:122:123:-65:-95:-115:-66:107:-39:14:-7:63:-25:96:60:14:89:37:-87:63:-21:-58:-21:78:50:-92:-52:63:-42:94:117:-69:-62:-68:-65:63:-16:0:0:0:0:0:0:63:-72:-46:-40:46:51:-76:70:63:-16:0:0:0:0:0:0:63:-30:-99:50:-76:-72:33:119:63:-31:-111:-92:5:-87:119:117:63:-16:0:0:0:0:0:0:63:-59:-28:98:35:-94:34:47:-65:-26:86:-30:-14:55:-66:-94:120:115:113:0:126:0:15:115:113:0:126:0:22:0:0:0:50:0:0:0:49:115:113:0:126:0:24:63:-16:0:0:0:0:0:0:115:113:0:126:0:24:-65:-16:0:0:0:0:0:0:119:4:0:0:0:49:113:0:126:0:65:122:0:0:1:-104:-65:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:-65:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:-65:-49:-16:-51:96:-108:-88:73:-65:-26:-83:-112:126:0:88:100:-65:-31:5:-123:22:-112:-41:-89:-65:-111:-90:-24:91:-99:-118:-128:-65:-34:22:91:-111:120:-101:-104:63:-16:0:0:0:0:0:0:-65:-16:0:0:0:0:0:0:63:-32:-52:-40:-84:50:5:18:-65:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:63:-41:110:48:120:-54:25:-76:-65:-34:61:102:-42:34:60:-13:63:-57:-60:-12:-37:-3:-22:108:-65:-16:0:0:0:0:0:0:-65:-17:3:-83:-63:64:109:-96:-65:-24:23:-72:-88:18:46:105:63:-16:0:0:0:0:0:0:63:-20:-15:92:-62:104:53:-85:63:-20:-102:115:-99:-51:36:-41:-65:-36:-99:96:-48:-128:64:26:-65:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:-65:-80:-113:-89:-110:-119:31:-48:-65:-17:-12:105:51:52:35:-125:-65:-27:-125:-97:107:7:72:87:-65:-28:52:32:115:-4:-92:84:63:-22:-99:23:7:108:75:3:-65:-16:0:0:0:0:0:0:63:-56:-87:-74:6:-89:125:32:63:-16:0:0:0:0:0:0:63:-77:92:70:39:113:-34:87:63:-73:-9:45:-27:-123:25:-96:-65:-16:0:0:0:0:0:0:-65:-17:-56:3:-92:-18:88:-24:-65:-45:51:116:-114:80:57:-52:63:-19:-78:26:127:22:29:43:-65:-22:19:79:95:-32:-18:74:-65:-37:14:36:-32:-66:-66:116:-65:-17:-118:61:-2:119:39:100:63:-32:120:-46:-89:-92:36:118:-65:-18:0:-111:-107:92:121:-95:63:-25:-71:15:-78:54:-30:66:63:-49:94:-52:78:-18:-93:40:63:-16:0:0:0:0:0:0:63:-36:-41:-6:-74:36:-93:-122:-65:-16:0:0:0:0:0:0:63:-70:101:-99:12:104:120:-56:120:115:113:0:126:0:15:115:113:0:126:0:22:0:0:0:50:0:0:0:49:115:113:0:126:0:24:63:-16:0:0:0:0:0:0:115:113:0:126:0:24:-65:-16:0:0:0:0:0:0:119:4:0:0:0:49:113:0:126:0:69:122:0:0:1:-104:-65:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:63:-19:-65:39:-24:103:-33:-33:63:-23:-38:-66:-7:122:65:17:-65:-16:0:0:0:0:0:0:63:-58:106:6:-116:72:-62:-7:-65:-16:0:0:0:0:0:0:63:-17:-66:-97:80:90:106:44:-65:-34:-125:-78:-31:119:-107:-66:-65:-33:82:-84:112:-97:115:-100:-65:-17:-78:-17:92:11:33:-30:-65:-47:13:-47:57:-25:-85:-12:63:-20:-111:65:-15:69:-33:122:-65:-72:-79:-30:109:-90:112:120:-65:-82:45:-51:-49:69:-113:8:-65:-26:-47:8:69:-36:117:88:-65:-16:0:0:0:0:0:0:-65:-76:50:-70:9:35:36:56:-65:-27:-82:-83:-72:-37:-2:115:63:-16:0:0:0:0:0:0:63:-44:-94:49:117:-75:-16:-128:-65:-80:120:-63:0:111:-21:48:63:-29:29:-112:-120:125:100:69:-65:-16:0:0:0:0:0:0:63:-81:-69:3:-77:-31:-63:-104:63:-69:-119:-76:-85:-116:102:-90:63:-47:127:-30:16:-64:112:-124:-65:-37:32:-3:75:127:47:22:63:-62:-9:52:-65:-99:82:-112:-65:-86:63:81:-55:-29:120:24:63:-16:0:0:0:0:0:0:-65:-16:0:0:0:0:0:0:63:106:-82:101:3:-126:116:0:-65:-16:0:0:0:0:0:0:-65:-25:75:30:51:-24:7:36:-65:-92:-81:67:125:69:56:-120:-65:-22:-29:-95:110:31:47:27:-65:-43:-85:-94:-11:-88:-119:-117:63:-20:30:82:18:-49:116:67:-65:-16:0:0:0:0:0:0:-65:-16:0:0:0:0:0:0:63:-25:-49:87:42:-80:-52:103:63:-58:72:-125:-96:84:101:-42:-65:-16:0:0:0:0:0:0:-65:-18:99:-31:-68:59:-79:88:63:-64:70:110:125:87:-115:52:-65:-24:69:35:36:-41:-91:74:-65:-16:0:0:0:0:0:0:-65:-16:0:0:0:0:0:0:63:-57:-84:81:101:-22:-85:-56:63:-16:0:0:0:0:0:0:120:115:113:0:126:0:15:115:113:0:126:0:22:0:0:0:50:0:0:0:49:115:113:0:126:0:24:63:-16:0:0:0:0:0:0:115:113:0:126:0:24:-65:-16:0:0:0:0:0:0:119:4:0:0:0:49:113:0:126:0:73:122:0:0:1:-104:-65:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:-65:-39:-127:-80:-83:-58:108:-30:63:-37:-112:14:52:32:-1:-102:63:-16:0:0:0:0:0:0:-65:-52:58:61:-15:49:125:-75:63:-23:71:117:-78:84:102:-108:63:-25:113:-34:-6:53:97:-121:-65:-114:19:1:46:-111:-54:64:63:-17:-107:-66:117:-85:76:-119:-65:-80:-5:-64:-57:106:29:-128:-65:-16:0:0:0:0:0:0:-65:-16:0:0:0:0:0:0:-65:-28:59:-120:-114:-78:6:38:63:-16:0:0:0:0:0:0:-65:-92:23:-5:-14:-100:1:42:63:-43:-29:16:-27:89:18:30:63:-18:12:-4:-49:-88:74:11:-65:-23:-14:98:-6:-22:55:-48:-65:-23:63:-87:-21:-66:13:-4:-65:-16:0:0:0:0:0:0:63:-112:-11:111:50:127:40:-128:63:-19:19:99:115:-89:-52:-35:63:122:66:74:13:56:57:-128:-65:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:-65:-35:88:-18:-80:119:-37:-117:63:-71:72:123:-42:-43:12:-50:-65:-17:96:33:57:60:-26:-71:-65:-29:-118:-87:-69:-37:-104:101:-65:-21:40:17:73:-28:68:116:-65:-54:118:123:108:66:-76:-112:63:-21:-98:-74:120:-114:-33:-122:-65:-22:-26:-31:-5:-30:-76:-43:-65:-67:-118:38:-98:99:-98:-72:63:-22:-41:-43:-112:-37:53:7:63:-90:48:109:-51:-109:94:88:63:-54:79:-104:23:10:57:-51:63:-29:56:70:-117:-84:50:-96:63:-31:-75:103:-67:107:122:95:-65:-17:-33:-89:28:-95:2:58:63:-37:-54:-10:35:67:94:19:-65:-16:0:0:0:0:0:0:63:-60:-15:-77:89:1:123:-124:63:-22:-66:123:27:67:-82:24:-65:-16:0:0:0:0:0:0:-65:-32:86:-91:22:-115:-94:-96:63:-16:0:0:0:0:0:0:-65:-37:100:1:39:41:-126:-63:-65:-34:-35:-26:9:-117:-69:54:63:-31:29:11:-69:-21:31:-61:120:115:113:0:126:0:15:115:113:0:126:0:22:0:0:0:50:0:0:0:49:115:113:0:126:0:24:63:-16:0:0:0:0:0:0:115:113:0:126:0:24:-65:-16:0:0:0:0:0:0:119:4:0:0:0:49:113:0:126:0:77:122:0:0:1:-104:-65:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:-65:-16:0:0:0:0:0:0:-65:-16:0:0:0:0:0:0:-65:-109:-40:40:-13:-49:-78:-120:-65:-51:97:-1:-18:104:78:96:-65:-50:15:-64:-113:-21:84:36:-65:-28:-121:-73:-29:59:-44:-31:-65:-16:0:0:0:0:0:0:63:-28:74:122:-52:-75:-125:-25:-65:-73:83:8:-47:-125:16:58:-65:-25:-18:38:-43:-86:-16:-86:63:-33:35:102:52:69:70:-10:-65:-33:106:-33:-103:-86:111:-66:-65:-19:43:-108:-10:101:-119:-58:-65:-78:114:35:-99:19:20:-4:63:-38:107:108:-119:-58:-74:83:63:-16:0:0:0:0:0:0:63:-26:-78:-92:-122:78:-90:-35:-65:-24:69:49:14:-70:29:33:63:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:-65:-17:-15:-81:16:22:-19:54:-65:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:63:-36:-123:47:-14:-107:98:-38:-65:-65:-91:-85:-86:-59:-80:9:-65:-16:0:0:0:0:0:0:63:-71:109:10:-55:120:-83:72:63:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:63:-17:56:-42:-110:71:25:19:-65:-16:0:0:0:0:0:0:63:-29:-81:-53:-71:-94:23:-124:-65:-34:-54:-111:82:-23:5:52:-65:-16:0:0:0:0:0:0:63:-29:90:-102:-62:-9:119:8:63:-21:29:-8:0:13:31:116:63:-47:-2:59:23:-89:-56:110:63:-55:-94:-97:-94:82:16:-83:63:-18:-65:-79:106:-55:-86:86:63:-16:0:0:0:0:0:0:-65:-22:-58:20:-109:-111:22:-124:-65:-16:0:0:0:0:0:0:63:-22:-40:4:37:44:-116:88:-65:-73:83:115:45:-117:51:8:63:-44:-9:105:-16:-79:-25:-58:-65:-60:52:105:-66:26:-92:-93:63:-16:0:0:0:0:0:0:-65:-45:88:48:18:42:48:-88:120:115:113:0:126:0:15:115:113:0:126:0:22:0:0:0:50:0:0:0:49:115:113:0:126:0:24:63:-16:0:0:0:0:0:0:115:113:0:126:0:24:-65:-16:0:0:0:0:0:0:119:4:0:0:0:49:113:0:126:0:81:122:0:0:1:-104:-65:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:63:-60:13:-73:-43:-45:-112:-67:-65:-26:-73:10:-2:23:86:-59:-65:-48:-88:53:-81:29:-67:-62:-65:-18:-67:-53:-28:-114:88:10:-65:-24:20:-48:10:-126:102:-74:63:-16:0:0:0:0:0:0:-65:-16:0:0:0:0:0:0:-65:-27:108:55:101:-24:65:93:63:-36:-103:-6:22:8:-83:-50:63:-51:112:-40:-85:18:-56:-48:-65:-83:-66:122:-31:34:65:-128:63:-19:60:77:-127:51:97:-48:-65:-16:0:0:0:0:0:0:-65:-16:0:0:0:0:0:0:-65:-31:98:64:114:90:-35:-75:-65:-26:76:84:-114:50:-117:-57:-65:-70:48:-126:100:-102:44:12:63:-30:75:-96:57:88:4:118:-65:-33:-45:-61:126:-60:-72:61:-65:-60:-24:66:122:59:-83:82:63:-54:-43:-21:-117:88:122:92:-65:-37:-33:-85:-119:58:-97:-33:-65:-16:0:0:0:0:0:0:-65:-78:47:106:22:16:-41:-114:-65:-61:-44:-92:102:-87:72:48:-65:-33:-1:75:-31:21:40:-4:-65:-20:-70:-40:-11:22:29:-9:-65:-30:108:-92:-73:37:38:-64:-65:-16:0:0:0:0:0:0:63:-34:-88:-66:-79:119:-62:-31:63:-61:85:-80:55:-8:-19:104:63:-25:-102:-70:-53:53:125:22:63:-16:0:0:0:0:0:0:63:-66:-45:-125:-92:-42:112:12:-65:-55:-116:110:93:-76:104:-92:63:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:63:-60:34:-63:-36:-34:8:-14:-65:-34:-11:76:-47:54:-81:66:63:-40:94:-38:-84:11:-54:-74:-65:-16:0:0:0:0:0:0:-65:-105:-60:73:-20:75:12:-56:63:-26:110:89:62:20:-96:-124:63:-19:-3:98:103:78:-99:-72:-65:-43:74:98:-90:86:25:-46:-65:-62:-102:90:53:-127:-52:-121:-65:-26:-104:-81:-118:-50:-77:120:63:-27:-81:2:-7:-41:92:-81:-65:-22:27:-125:-66:96:81:-10:120:115:113:0:126:0:15:115:113:0:126:0:22:0:0:0:50:0:0:0:49:115:113:0:126:0:24:63:-16:0:0:0:0:0:0:115:113:0:126:0:24:-65:-16:0:0:0:0:0:0:119:4:0:0:0:49:113:0:126:0:85:122:0:0:1:-104:-65:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:-65:-61:42:74:3:-108:125:68:63:-41:82:-59:-14:111:11:-100:63:-16:0:0:0:0:0:0:63:-23:-59:76:-88:21:115:122:63:-16:0:0:0:0:0:0:-65:-51:-77:94:-102:12:80:-124:63:-16:0:0:0:0:0:0:-65:-16:0:0:0:0:0:0:-65:-16:0:0:0:0:0:0:-65:-32:32:93:77:-20:56:60:63:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:-65:-16:0:0:0:0:0:0:63:-60:-60:-96:-80:69:34:36:63:-16:0:0:0:0:0:0:63:-55:-3:-56:-33:20:-21:69:63:124:114:88:-80:-48:-113:0:63:-48:63:-93:-119:-77:104:98:-65:-16:0:0:0:0:0:0:-65:-43:118:-10:-74:-28:-54:70:-65:-22:-65:-6:23:2:127:-110:-65:-96:-125:80:126:-2:68:120:-65:-29:32:-42:40:53:-6:-72:63:-22:95:94:40:-76:-92:-37:63:-16:0:0:0:0:0:0:-65:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:63:-37:-87:3:39:10:82:-86:-65:-29:-100:124:-81:-49:34:111:-65:-16:0:0:0:0:0:0:-65:-44:-19:11:123:-125:-15:114:63:-66:104:-55:87:125:83:-56:-65:-22:43:16:-83:30:27:64:-65:-31:-65:-116:-25:122:36:119:63:-16:0:0:0:0:0:0:-65:-43:-114:-35:93:67:24:-67:-65:-16:0:0:0:0:0:0:-65:-36:19:-30:3:-12:12:42:63:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:63:-54:-79:47:-61:-10:115:-88:63:-60:122:-82:115:30:77:101:-65:-17:-62:-31:-123:121:-86:-34:-65:-41:-88:121:-106:-56:-127:-9:63:-48:71:46:38:-76:-22:87:-65:-19:-88:16:57:19:41:-29:63:-16:0:0:0:0:0:0:63:-28:20:-8:-122:-14:88:-84:-65:-64:-49:94:-70:-106:-50:-96:120:115:113:0:126:0:15:115:113:0:126:0:22:0:0:0:5:0:0:0:4:115:113:0:126:0:24:63:-16:0:0:0:0:0:0:115:113:0:126:0:24:-65:-16:0:0:0:0:0:0:119:4:0:0:0:4:113:0:126:0:89:119:48:-65:-16:0:0:0:0:0:0:63:-16:0:0:0:0:0:0:-65:-44:111:-124:-12:98:114:2:-65:-16:0:0:0:0:0:0:63:-53:-105:33:-27:-128:72:-45:63:-43:-70:100:7:-56:2:67:120:120:115:114:0:17:106:97:118:97:46:108:97:110:103:46:66:111:111:108:101:97:110:-51:32:114:-128:-43:-100:-6:-18:2:0:1:90:0:5:118:97:108:117:101:120:112:1")!!)