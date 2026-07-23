const { request } = require('./request');

const categoryIcons = ['🍚', '🍗', '🥗', '🥤', '🍱', '🍰'];
const categoryColors = ['#fee2e2', '#ffedd5', '#fef3c7', '#dbeafe', '#dcfce7', '#fce7f3'];

/**
 * 将后端分类转换为现有页面展示所需的数据。
 */
function toCategory(category, index) {
  return {
    ...category,
    icon: categoryIcons[index % categoryIcons.length],
    bg: categoryColors[index % categoryColors.length]
  };
}

/**
 * 将后端菜品转换为购物车和页面共用的数据结构。
 */
function toDish(dish, category) {
  return {
    ...dish,
    category: category.name,
    desc: dish.description,
    productType: 'dish',
    hasSpecs: dish.flavors.length > 0
  };
}

/**
 * 将后端套餐转换为购物车和页面共用的数据结构。
 */
function toSetmeal(setmeal, category) {
  return {
    ...setmeal,
    category: category.name,
    desc: setmeal.description,
    productType: 'setmeal',
    hasSpecs: false
  };
}

/**
 * 加载用户端商品浏览所需的全部分类、菜品和套餐。
 */
async function loadProductData() {
  const [dishCategories, setmealCategories] = await Promise.all([
    request({
      url: '/user/category/list?type=1',
      method: 'GET'
    }),
    request({
      url: '/user/category/list?type=2',
      method: 'GET'
    })
  ]);

  const categories = dishCategories
    .concat(setmealCategories)
    .map((category, index) => toCategory(category, index));

  const productLists = await Promise.all(categories.map(async category => {
    if (category.type === 1) {
      const dishes = await request({
        url: `/user/dish/list?categoryId=${category.id}`,
        method: 'GET'
      });

      return dishes.map(dish => toDish(dish, category));
    }

    const setmeals = await request({
      url: `/user/setmeal/list?categoryId=${category.id}`,
      method: 'GET'
    });

    return setmeals.map(setmeal => toSetmeal(setmeal, category));
  }));

  return {
    categories,
    foods: productLists.flat()
  };
}

module.exports = {
  loadProductData
};
