import '@vaadin/button';
import '@vaadin/checkbox';
import '@vaadin/date-picker';
import '@vaadin/date-time-picker';
import '@vaadin/form-layout';
import '@vaadin/grid';
import '@vaadin/grid/vaadin-grid-column';
import '@vaadin/horizontal-layout';
import '@vaadin/split-layout';
import '@vaadin/text-field';
import { html, LitElement } from 'lit';
import { customElement } from 'lit/decorators.js';

@customElement('master-detail-sample-address-javahtml-view')
export class MasterDetailSampleAddressJavahtmlView extends LitElement {
  createRenderRoot() {
    // Do not use a shadow root
    return this;
  }

  render() {
    return html`<vaadin-split-layout>
      <div class="grid-wrapper">
        <vaadin-grid id="grid"></vaadin-grid>
      </div>
      <div class="editor-layout">
        <div class="editor">
          <vaadin-form-layout>
            <vaadin-text-field label="Street" id="street"></vaadin-text-field
            ><vaadin-text-field label="Postal code" id="postalCode"></vaadin-text-field
            ><vaadin-text-field label="City" id="city"></vaadin-text-field
            ><vaadin-text-field label="State" id="state"></vaadin-text-field
            ><vaadin-text-field label="Country" id="country"></vaadin-text-field>
          </vaadin-form-layout>
        </div>
        <vaadin-horizontal-layout class="button-layout">
          <vaadin-button theme="primary" id="save">Save</vaadin-button>
          <vaadin-button theme="tertiary" slot="" id="cancel">Cancel</vaadin-button>
        </vaadin-horizontal-layout>
      </div>
    </vaadin-split-layout>`;
  }
}
