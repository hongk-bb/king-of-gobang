import {createStore} from 'vuex'
import ModulUser from '@/store/user'
import ModulePk from '@/store/pk'
import ModuleRecord from '@/store/record'

export default createStore({
  state: {},
  getters: {},
  mutations: {},
  actions: {},
  modules: {
    user: ModulUser,
    pk: ModulePk,
    record: ModuleRecord,
  }
})
