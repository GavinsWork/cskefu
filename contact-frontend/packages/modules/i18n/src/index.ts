import { App } from 'vue'
import { createI18n } from 'vue-i18n'
import _ from 'lodash-es'
import { useLocalStorage } from '@vueuse/core'

import { Apperance } from '@cskefu/models'

/**
 * 语言支持类型
 */
export const locales: string[] = ['zh-CN', 'en-US']
export const defaultLocale = 'zh-CN'
const localPathPrefix = './locales/'

const getValidLocale = (defaults: string = defaultLocale): string => {
  const storage = useLocalStorage<Apperance>('apperance', {
    lang: defaults,
    darkMode: false,
  })

  const param = new URLSearchParams(window.location.search).get('lang')
  if (param && locales.includes(param)) {
    storage.value.lang = param
  }

  const lang = storage.value?.lang || defaults
  if (locales.includes(lang)) {
    return lang
  }

  return defaults
}

const mergedLocalMessage = Object.entries(
  import.meta.glob('./locales/**/*.json', { eager: true })
).reduce(
  (map, [key, value]: [string, any]) => {
    const name = key.slice(localPathPrefix.length, -5)
    const sections = name.split('/')

    if (sections.length === 1) {
      map[name] = _.merge(value.default, map[name] || {})
    } else {
      const file = sections.slice(-1)[0]
      const sectionsName = sections[0]
      const existed = map[file] || {}
      map[file] = {
        ...existed,
        [sectionsName]: _.merge(value.default, existed[sectionsName] || {}),
      }
    }

    return map
  },
  {} as { [k: string]: any }
)

const i18n = createI18n({
  legacy: false,
  locale: getValidLocale(defaultLocale),
  messages: mergedLocalMessage,
  globalInjection: true,
  fallbackLocale: defaultLocale,
})

const install = (app: App) => {
  app.use(i18n)
}

export default install