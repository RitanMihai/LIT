import { entityItemSelector } from '../../support/commands';
import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Stock e2e test', () => {
  const stockPageUrl = '/stock';
  const stockPageUrlPattern = new RegExp('/stock(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const stockSample = { ticker: 'Place compressing withdrawal' };

  let stock: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/services/watcher/api/stocks+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/watcher/api/stocks').as('postEntityRequest');
    cy.intercept('DELETE', '/services/watcher/api/stocks/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (stock) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/watcher/api/stocks/${stock.id}`,
      }).then(() => {
        stock = undefined;
      });
    }
  });

  it('Stocks menu should load Stocks page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('stock');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Stock').should('exist');
    cy.url().should('match', stockPageUrlPattern);
  });

  describe('Stock page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(stockPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Stock page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/stock/new$'));
        cy.getEntityCreateUpdateHeading('Stock');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', stockPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/watcher/api/stocks',
          body: stockSample,
        }).then(({ body }) => {
          stock = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/watcher/api/stocks+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [stock],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(stockPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Stock page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('stock');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', stockPageUrlPattern);
      });

      it('edit button click should load edit Stock page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Stock');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', stockPageUrlPattern);
      });

      it('last delete button click should delete instance of Stock', () => {
        cy.intercept('GET', '/services/watcher/api/stocks/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('stock').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', stockPageUrlPattern);

        stock = undefined;
      });
    });
  });

  describe('new Stock page', () => {
    beforeEach(() => {
      cy.visit(`${stockPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Stock');
    });

    it('should create an instance of Stock', () => {
      cy.get(`[data-cy="ticker"]`).type('Grocery invoice impactful').should('have.value', 'Grocery invoice impactful');

      cy.get(`[data-cy="name"]`).type('Soap Concrete').should('have.value', 'Soap Concrete');

      cy.setFieldImageAsBytesOfEntity('image', 'integration-test.png', 'image/png');

      cy.get(`[data-cy="marketCap"]`).type('optimizing Rufiyaa deposit').should('have.value', 'optimizing Rufiyaa deposit');

      cy.get(`[data-cy="volume"]`).type('52503').should('have.value', '52503');

      cy.get(`[data-cy="peRation"]`).type('73577').should('have.value', '73577');

      cy.get(`[data-cy="ipoDate"]`).type('2022-03-28').should('have.value', '2022-03-28');

      cy.get(`[data-cy="isin"]`).type('Brazilian indexing firewall').should('have.value', 'Brazilian indexing firewall');

      cy.get(`[data-cy="isDelisted"]`).should('not.be.checked');
      cy.get(`[data-cy="isDelisted"]`).click().should('be.checked');

      cy.get(`[data-cy="hasDividend"]`).should('not.be.checked');
      cy.get(`[data-cy="hasDividend"]`).click().should('be.checked');

      cy.get(`[data-cy="type"]`).select('COMMON');

      cy.get(`[data-cy="dividendYield"]`).type('33146').should('have.value', '33146');

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        stock = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', stockPageUrlPattern);
    });
  });
});
